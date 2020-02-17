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
  	<link rel="stylesheet" type="text/css" href="assets/css/animsition.min.css">
  	<link rel="stylesheet" type="text/css" href="assets/css/style.css">
  	<link rel="stylesheet" type="text/css" href="assets/css/index.css"> 
  	
</head>
<body>
  
  <div class="overlay"></div>

  <nav class="navbar navbar-default navbar-fixed-top my-navbar" role="navigation">
    <div class="container-fluid container my-nav">
      <div class="navbar-header">
        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#menu">
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
        </button>
        <span class="navbar-brand"><!-- 想不出名字 --></span>
      </div>
      <div id="menu" class="navbar-collapse collapse">
        <ul class="nav navbar-nav navbar-right my-navbar-menu">
		  <li class="btn-2"><a href="#" data-toggle="modal" data-target="#myModal">介绍</a></li>		
        </ul>
      </div>
    </div>
  </nav>
	
  <div class="animsition">
  <main>
    <div class="container">
      <div class="row center-content">
        <div id="frame">
		  <div class="resumable-error">
			   Your browser, unfortunately, is not supported by Resumable.js. 
			   The library requires support for <a href="http://www.w3.org/TR/FileAPI/">the HTML5 File API</a> 
			   along with <a href="http://www.w3.org/TR/FileAPI/#normalization-of-params">file slicing</a>.
		  </div>
		  <div class="resumable-drop" ondragenter="jQuery(this).addClass('resumable-dragover');" 
			   ondragend="jQuery(this).removeClass('resumable-dragover');" 
			   ondrop="jQuery(this).removeClass('resumable-dragover');">
			   Drop video files here to upload or 
			   <a class="resumable-browse">select from your computers</a>
		  </div>      
		  <div class="resumable-progress">
			<table>
			  <tr>
			    <td width="100%"><div class="progress-container"><div class="progress-bar"></div></div></td>
			    <td class="progress-text" nowrap="nowrap"></td>
			  </tr>
			</table>
	      </div>
		  <ul class="resumable-list"></ul>
		</div>
      </div>
    </div>    
  </main>
 
  <footer class="footer">
	<a href="showAllVideos" class="btn btn-md my-btn-border animsition-link" 
		data-animsition-out="fade-out-up" type="button">
	   <i class="fa fa-chevron-down fa-2x" aria-hidden="true"></i>
	</a>
  </footer>
  </div>
 
  <div class="modal fade" style="position: fixed;" id="myModal"
			tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
			aria-hidden="true">
	<div class="modal-dialog">
	  <div class="modal-content">
	    <div class="modal-header">
		  <button type="button" class="close" data-dismiss="modal"
			aria-hidden="true" onclick="stop()">&times;</button>
			<h4 class="modal-title" id="myModalLabel">DEMO</h4>
		</div>
		<div class="modal-body">
		  <video id="demo" controls="controls" loop="loop" preload="auto" width="100%" height="100%" poster="assets/images/#">
		     <source src="#" type="video/mp4">您的浏览器不支持该格式，推荐使用chrome浏览器
	      </video>
		</div>
		<div class="modal-footer">
			观看以上的演示视频，让您更好地使用我们的网站~
		</div>
	  </div>
	</div>	
  </div>
  
 
 
  <script type="text/javascript" src="assets/js/jquery-2.1.4.min.js"></script>
  <script type="text/javascript" src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <script type="text/javascript" src="assets/js/resumable.js"></script>
  <script type="text/javascript" src="assets/js/jquery.animsition.min.js"></script>
  
  <script>
		var r = new Resumable({
	        target:'upload_get',
	        chunkSize:1*1024*1024,
	        simultaneousUploads:4,
	        testChunks: true,
	        throttleProgressCallbacks:1,
	        method: "octet"
	      });
	    // Resumable.js isn't supported, fall back on a different method
	    if(!r.support) {
	      $('.resumable-error').show();
	    } else {
	      // Show a place for dropping/selecting files
	      $('.resumable-drop').show();
	      r.assignDrop($('.resumable-drop')[0]);
	      r.assignBrowse($('.resumable-browse')[0]);
	
	      // Handle file add event
	      r.on('fileAdded', function(file){
	          // Show progress pabr
	          $('.resumable-progress, .resumable-list').show();
	          // Show pause, hide resume
	          $('.resumable-progress .progress-resume-link').hide();
	          $('.resumable-progress .progress-pause-link').show();
	          // Add the file to the list
	          $('.resumable-list').append('<li class="resumable-file-'+file.uniqueIdentifier+'">Uploading <span class="resumable-file-name"></span> <span class="resumable-file-progress"></span>');
	          $('.resumable-file-'+file.uniqueIdentifier+' .resumable-file-name').html(file.fileName);
	          // Actually start the upload
	          r.upload();
	        });
	      r.on('pause', function(){
	          // Show resume, hide pause
	          $('.resumable-progress .progress-resume-link').show();
	          $('.resumable-progress .progress-pause-link').hide();
	        });
	      r.on('complete', function(){
	          // Hide pause/resume when the upload has completed
	          $('.resumable-progress .progress-resume-link, .resumable-progress .progress-pause-link').hide();
	        });
	      r.on('fileSuccess', function(file,message){
	          // Reflect that the file upload has completed
	          $('.resumable-file-'+file.uniqueIdentifier+' .resumable-file-progress').html('(completed)');
	        });
	      r.on('fileError', function(file, message){
	          // Reflect that the file upload has resulted in error
	          $('.resumable-file-'+file.uniqueIdentifier+' .resumable-file-progress').html('(file could not be uploaded: '+message+')');
	        });
	      r.on('fileProgress', function(file){
	          // Handle progress for both the file and the overall upload
	          $('.resumable-file-'+file.uniqueIdentifier+' .resumable-file-progress').html(Math.floor(file.progress()*100) + '%');
	          $('.progress-bar').css({width:Math.floor(r.progress()*100) + '%'});
	        });
	    }
	</script>
	
  	<script type="text/javascript">
	  	function centerModals(){
		    $('.modal').each(function(i){   //遍历每一个模态框
		        var $clone = $(this).clone().css('display', 'block').appendTo('body');    
		        var top = Math.round(($clone.height() - $clone.find('.modal-content').height()) / 2);
		        top = top > 0 ? top : 0;
		        $clone.remove();
		        $(this).find('.modal-content').css("margin-top", top-30);  //修正原先已经有的30个像素
		    });
		}
		$('.modal').on('show.bs.modal', centerModals);      //当模态框出现的时候
		$(window).on('resize', centerModals);               //当窗口大小变化的时候
		
		function stop() {
			var demo = document.getElementById('demo');
			demo.pause();
	        demo.currentTime = 0;
		}
		
		//模态框初始化时不会显示，且禁用点击空白页面或escape关闭模态框
		$('#myModal').modal({show:false, keyboard: false, backdrop:false});
		
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
			    //"unSupportCss" option allows you to disable the "animsition" in case the css property in the array is not supported by your browser.
			    //The default setting is to disable the "animsition" in a browser that does not support "animation-duration".
			     
			    overlay               :   false,
			     
			    overlayClass          :   'animsition-overlay-slide',
			    overlayParentElement  :   'body'
			  });
			});            
		
		
  	</script>
</body>
</html>
