<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html>
<html lang="en">
<head>
  	<meta charset="UTF-8">
  	<meta name="viewport" content="width=device-width, initial-scale=1.0">
  	
  	<title>首页</title>

  	<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  	<link rel="stylesheet" type="text/css"
		href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.6.3/css/font-awesome.min.css"> 
  	<link rel="stylesheet" type="text/css" href="assets/css/jquery.restable.min.css">
  	<link rel="stylesheet" type="text/css" href="assets/css/animsition.min.css">
  	<link rel="stylesheet" type="text/css" href="assets/css/main.css">
  	<link rel="stylesheet" type="text/css" href="assets/css/index.css"> 
  	
</head>
<body>
  <div class="overlay"></div>
  
  <div class="animsition">
  <header>
	<a href="index" class="btn btn-md my-btn-border animsition-link" 
		data-animsition-out="fade-out-down" type="button">
	   <i class="fa fa-chevron-up fa-2x" aria-hidden="true"></i>
	</a>
  </header>
  
  <div class="main-2">
    <div class="container">
      <div class="row center-content">
        <table class="mytable">
		    <thead>
		        <tr>
		            <td>视频名称</td>
		        </tr>
		    </thead>
		    <tbody>
		    <s:iterator value = "videoList" id = "vl">
		        <tr>
		            <td><a href = "play?name=<s:property value = "vl" />"><s:property value = "vl" /></a></td>
		        </tr>
		        </s:iterator>
		    </tbody>
		</table>       
      </div>
    </div>
  	      
  </div>
  </div>
  
  <script type="text/javascript" src="assets/js/jquery-2.1.4.min.js"></script>
  <script type="text/javascript" src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <script type="text/javascript" src="assets/js/jquery.animsition.min.js"></script>
  <script type="text/javascript" src="assets/js/jquery.restable.min.js"></script>
  
  <script>
	  $(document).ready(function() {
		  $(".animsition").animsition({
		   
		    inClass               :   'fade-in',
		    outClass              :   'fade-out',
		    inDuration            :    1500,
		    outDuration           :    800,
		    linkElement           :   '.animsition-link',
		    // e.g. linkElement   :   'a:not([target="_blank"]):not([href^=#])'
		    loading               :    true,
		    loadingParentElement  :   'body', //animsition wrapper element
		    loadingClass          :   'animsition-loading',
		    unSupportCss          : [ 'animation-duration',
		                              '-webkit-animation-duration',
		                              '-o-animation-duration'
		                            ],
		    overlay               :   false,
		     
		    overlayClass          :   'animsition-overlay-slide',
		    overlayParentElement  :   'body'
		  });
		 
		  $('.mytable').ReStable({
			  rowHeaders: true, 
			  keepHtml: false
		  });
		  
		});            
	</script>
</body>
</html>
