package com.herokuapp.lzqwebsoft.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.herokuapp.lzqwebsoft.pojo.Comment;
import com.herokuapp.lzqwebsoft.pojo.User;
import com.herokuapp.lzqwebsoft.service.CommentService;
import com.herokuapp.lzqwebsoft.util.CommonConstant;

@Controller
public class CommentController {
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private MessageSource messageSource;
	
	@RequestMapping("/comment/add")
	public String add(Comment comment, String parent_comment_id,
			ModelMap model, HttpServletRequest request,
			HttpSession session, HttpServletResponse response) {
	    // 判断用户是否登录,则博主不用输入昵称与网址
        User user = (User)session.getAttribute(CommonConstant.LOGIN_USER);
        if(user!=null) {
            comment.setReviewer(user.getUserName());
            comment.setWebsite("http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath());
            // 标记此条评论由博主产生
            comment.setIsBlogger(true);
        }
        
		// 验证数据的合法性
		Locale locale = request.getLocale();
		List<String> errors = new ArrayList<String>();
		String reviewer = comment.getReviewer();
		String contentStr = comment.getContent();
		String website = comment.getWebsite();
		if(reviewer==null||reviewer.trim().length()<=0) {
			String reviewerLabel = messageSource.getMessage("page.label.reviewer", null, locale);
			errors.add(messageSource.getMessage("info.required", new Object[]{reviewerLabel}, locale));
		} else if(reviewer.trim().length()>80) {
		    String reviewerLabel = messageSource.getMessage("page.label.reviewer", null, locale);
		    errors.add(messageSource.getMessage("info.length.long", new Object[]{reviewerLabel, 80}, locale));
		}
		if(website!=null&&website.trim().length()>0&&!website.matches("^((http[s]?)(://))(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*$")) {
		    errors.add(messageSource.getMessage("info.invalid.website", null, locale));
		}
		if(contentStr==null||contentStr.trim().length()<=0) {
			String contentLabel = messageSource.getMessage("page.label.content", null, locale);
			errors.add(messageSource.getMessage("info.required", new Object[]{contentLabel}, locale));
		} else {
		    contentStr = contentStr.toLowerCase();
		    contentStr = contentStr.replaceAll("<(img|embed).*?>", "K");
		    contentStr = contentStr.replaceAll("<.*?>", "");
		    contentStr = contentStr.replaceAll("\r\n|\n|\r/g", "");
		    contentStr = contentStr.trim();
		    if(contentStr.length()>120) {
		        String contentLabel = messageSource.getMessage("page.label.content", null, locale);
		        errors.add(messageSource.getMessage("info.length.long", new Object[]{contentLabel, 120}, locale));
		    }
		}
		if(errors.size()>0) {
			PrintWriter out = null;
			try {
				out = response.getWriter();
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json; charset=UTF-8");
				
				//生成错误的JSON信息，用于提于用户
				StringBuffer json = new StringBuffer("{\"status\": \"FAILURE\", \"messages\": [");
				for(String error : errors)
					json.append("\"").append(error).append("\",");
				json.deleteCharAt(json.length()-1);
				json.append("]}");
				
				out.print(json);
				out.close();
				return null;
			} catch(IOException e) {
				e.printStackTrace();
				if(out!=null) {
					out.close();
				}
			}
		}
		
		// 判断是否是子评论
		if(parent_comment_id!=null&&parent_comment_id.trim().length()>0) {
			long parentId = Long.parseLong(parent_comment_id);
			Comment parnetComment = new Comment();
			parnetComment.setId(parentId);
			comment.setParentComment(parnetComment);
		}
		
		commentService.save(comment);
		
		String articleId = comment.getArticle().getId();
		List<Comment> comments = commentService.getAllParentComment(articleId);
		model.addAttribute("comments", comments);
		
		return "_article_comments";
	}
	
	@RequestMapping("/comment/delete/{commentId}")
	public String delete(@PathVariable("commentId")long commentId, ModelMap model) {
		String articleId = commentService.delete(commentId);
		
		List<Comment> comments = commentService.getAllParentComment(articleId);
		model.addAttribute("comments", comments);
		
		Comment comment = new Comment();
		model.addAttribute("comment", comment);
		return "_article_comments";
	}
}
