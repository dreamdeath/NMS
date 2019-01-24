
'use strict';

(function($) {
	 
    $.ajaxSetup({           
        error: function(xhr, status, err) {
            if (xhr.readyState == 4) {
	            // HTTP error (can be checked by XMLHttpRequest.status and XMLHttpRequest.statusText)    	
            	 if (xhr.status == 401) {
	                    alert("세션이 종료 되어 로그인 페이지로 이동합니다.");
	                    document.location.href = "/login.do?expiredsession";
	             } else if (xhr.status == 403) {
	            	 alert("권한이 없습니다.");  
	             } else {
	                 alert("오류가 발생했습니다. 관리자에게 문의하세요.");
	             }
	        }        
        }
    });

})(jQuery);