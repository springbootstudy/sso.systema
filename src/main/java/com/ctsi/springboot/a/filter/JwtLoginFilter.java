package com.ctsi.springboot.a.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ctsi.springboot.a.entity.AjaxData;
import com.ctsi.springboot.a.util.Constants;
import com.ctsi.springboot.a.util.Http;
import com.ctsi.springboot.a.util.JacksonUtil;
import com.ctsi.springboot.a.util.JwtUtil;

@Component
@WebFilter( urlPatterns = {"/*"}, filterName = "jwtLoginFilter") 
public class JwtLoginFilter implements Filter  {
	
	private static final Logger log = Logger.getLogger(JwtLoginFilter.class);
	
    private List<String> excludedPageList;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		excludedPageList = new ArrayList<String>();
		Enumeration<String> enums = filterConfig.getInitParameterNames();
		String key;
		String value;
		while ( enums.hasMoreElements() ) {
			key = enums.nextElement();
			value = filterConfig.getInitParameter(key);
			
			log.info("## " + key + ", " + value);
			
			excludedPageList.add(value);
		}
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		log.info("## doFilter ");
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse rep = (HttpServletResponse) response;
//		req.getRequestURL();
		String query = req.getQueryString();
		String hostIpPort = request.getServerName() + ":" + request.getServerPort();
		log.info("## " + hostIpPort + ", " + query + ", " + req.getMethod() + ", " + req.getServletPath() + ", " + req.getUserPrincipal());
		
//		Enumeration<String> names = req.getAttributeNames();
//		while (names.hasMoreElements()) {
//			String name = names.nextElement();
//			System.out.println(name + ", " + req.getAttribute(name));
//		}
		
		rep.setHeader("Access-Control-Allow-Origin", "*");  
		
		// 判定是否预检请求
		if ("OPTIONS".equals(req.getMethod())) {
			log.info("## 处理预检");
			rep.setStatus(HttpStatus.NO_CONTENT.value());
			//当判定为预检请求后，设定允许请求的头部类型
			rep.setHeader("Access-Control-Allow-Headers", "Content-Type, x-requested-with, Token"); 
			//当判定为预检请求后，设定允许请求的方法
			rep.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, OPTIONS, DELETE");
			// 单位秒
			rep.addHeader("Access-Control-Max-Age", "60"); 
		}
		
		boolean isExcludedPage = false;
		for (String page : excludedPageList) {

			// 判断当前URL是否与例外页面相同
			if (req.getServletPath().equals(page)) { 
				log.info("## " + page + " , you're excluded.");
				isExcludedPage = true;
				break;
			}
			
		}
		
		if (isExcludedPage) { // 在过滤url之外
			chain.doFilter(request, response);
		} 
		else { // 不在过滤url之外
			response.setContentType("application/json; charset=utf-8");
			
			/*
			 * 这里需要验证 ST 是否有效
			 * 有效，生成 Token 写到响应头中，并返回正常的资源请求
			 */
			// 例子中只有一个参数，多个参数的情况下需要截取
			if (!StringUtils.isEmpty(query) && query.startsWith("st=")) {
				String url = "http://sso.sevenzero.org:8070/validateServiceTicket?" + query + "&system=" + hostIpPort;
				log.info("验证 ST 的地址 " + url);
				
				String result = Http.getStrContentByGet(url);
				log.info(result);
				if (!StringUtils.isEmpty(result)) {
					result = result.trim();
					// 验证成功
					if (Boolean.valueOf(result)) {
						log.info("验证成功");
						
						Map<String, Object> claims = new HashMap<>();
						String token = JwtUtil.generateToken(claims);
						
						rep.setHeader("token", token);
						
						chain.doFilter(request, response);
						return;
					}
				}
			}
			
			AjaxData ajaxData;
			String token = req.getHeader("token");
			log.info("## " + token);
			// 通过验证 true
			if (StringUtils.isEmpty(token)) {
				log.info("## token为空");
				try ( Writer writer = response.getWriter() ) {
					ajaxData = new AjaxData(1000, "请登录系统");
					writer.write(JacksonUtil.bean2Json(ajaxData));
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			// 否则 false
			else {
				try {
//					JwtUtil.validateToken(token);
					Claims claims = JwtUtil.getClaimsFromToken(token);
					log.info("## " + claims.get("username") + ", " + claims.get("userid") + ", " + claims.get(Constants.TOKEN_DATA));
					Date date = claims.getExpiration();
					long tokenTime = date.getTime();
					log.info("## 获取Token的时间 " + tokenTime + ", " + new Date(tokenTime));
					long curTime = System.currentTimeMillis();
					log.info("## 当前时间 " + curTime + ", " + new Date(curTime));
					
					if (curTime > tokenTime) {
						try ( Writer writer = response.getWriter() ) {
//							writer.write("token 过期，请重新获取");
							ajaxData = new AjaxData(1001, "token 过期，请重新获取");
							writer.write(JacksonUtil.bean2Json(ajaxData));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						
					}
					else {
						log.info("## 通过验证");
						req.setAttribute("tokenData", claims);
						chain.doFilter(request, response);
					}
				}
				catch (ExpiredJwtException ex) {
					log.info("## token 过期");
					ex.printStackTrace();
					try ( Writer writer = response.getWriter() ) {
//						writer.write("token 过期，请重新获取");
						ajaxData = new AjaxData(1001, "token 过期，请重新获取");
						writer.write(JacksonUtil.bean2Json(ajaxData));
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				catch (Exception ex) {
					ex.printStackTrace();
					log.info("## 解析token出错");
					
					try ( Writer writer = response.getWriter() ) {
//						writer.write("token 不正确");
						ajaxData = new AjaxData(1002, "token 不正确");
						writer.write(JacksonUtil.bean2Json(ajaxData));
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
		}
	}

	@Override
	public void destroy() {
		
	}
	

}
