<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>KT THUMBNAIL NMS LOGIN</title>
  <meta name="description" content="">
  <meta name="author" content="AmuzLab">
  <meta name="viewport" content="width=device-width, initial-scale=1">  
  <link rel="stylesheet" href="css/login/normalize.css">
  <link rel="stylesheet" href="css/login/login.css">
  <script type="text/javascript" src="js/lib/jquery-1.11.1.min.js"></script>
  <script>
	  $(document).ready(function () {   
		  $("#btnSignIn").on("click",function(){
				if($.trim($("#userid").val()) == ""){
					alert("사용자 아이디를 입력해주세요.");
					$("#userid").focus();
					return false;
					
				}
				if($.trim($("#password").val()) == ""){
					alert("사용자 비밀번호를  입력해주세요.");
					$("#password").focus();
					return false;
				}
				$("#frmLogin").submit();	  
		  });
	  });

  </script>
</head>
<body>
<div class="row">
	<div class="container">
		<div style="padding: 150px 50px 120px 50px;"><h2 class="title">KT THUMBNAIL NMS</h2></div>
		<c:url value="/authLogin.do" var="actionUrl"/>
		<form class="form-signin" name="frmLogin" id="frmLogin" action="${actionUrl}"  method="post">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<h4 class="section-heading"><span>SIGN IN</span></h4>
			<div class="row">
				<div class="column">
					<label>USERID </label>
					<input id="userid" name="username" class="full-width" type="text" placeholder="" value="">
				</div>
			</div>
			<div class="row">
				<div class="column">
					<label>PASSWORD </label>
					<input id="password" name="password" class="full-width" type="password" placeholder="" value="">
				</div>
			</div>
			<br/>
			<input class="button-submit" id="btnSignIn" type="button" value="SIGN IN">
			<c:if test="${param.error != null}">        
				<p>
					사용자 정보를 찾을 수 없습니다.
				</p>
			</c:if>
			<c:if test="${param.logout != null}">       
				<p>
					로그아웃 하셔습니다.
				</p>
			</c:if>
		</form>
	</div>
</div>
<!-- Copyrights by Mine Web Design-->
</body>
</html>