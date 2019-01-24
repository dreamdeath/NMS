<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
  <script>
	  $(document).ready(function () {   
		  var userSource =
          {
              datatype: "json",
              datafields: [
               { name: 'userid', type: 'string' },
               { name: 'username', type: 'username' },               
               { name: 'password', type: 'string' },
               { name: 'email', type: 'string' },
               { name: 'roleid', type: 'string' },
               { name: 'rolename', type: 'string' }                    
              ],
              id: 'userid',
              url: "/user/list.do",
              addrow: function (rowid, rowdata, position, commit) {
                   commit(true);
              },
              deleterow: function (rowid, commit) {
                  commit(true);
              },
              updaterow: function (rowid, newdata, commit) {
                  commit(true);
              }
	               
          };
          var userMgrDA = new $.jqx.dataAdapter(userSource);
			
          $("#userMgrGrid").jqxGrid(
          {		                  
	       	  	width:'100%',
	       	  	height: '99%',
	            filterable: true,				              
	       	    source: userMgrDA,
              columnsresize: true,
              sortable: true,
              pageable: true,
              pagermode: 'simple',
              showtoolbar : true,
              selectionmode : '',
              rendertoolbar: function (toolbar) {
                  var me = this;
                  var container = $("<div style='margin: 5px;'></div>");
                  toolbar.append(container);
                  container.append('<input id="addrowbutton" type="button" value="추가" />');                
                  container.append('<input style="margin-left: 5px;" id="updaterowbutton" type="button" value="수정" />');
                  container.append('<input style="margin-left: 5px;" id="deleterowbutton" type="button" value="삭제" />');	
                  $("#addrowbutton").jqxButton();
                  $("#deleterowbutton").jqxButton();
                  $("#updaterowbutton").jqxButton();
                  // update row.
                  $("#updaterowbutton").on('click', function () {
                	  var rowData = getSelectedRowData();
                	  $("#userId4Add").hide();
                	  $("#userId4Update").show();
                	  if(rowData) {
                      	openUserInfoPopup("update",rowData);
                	  } 
                  });
                  // create new row.
                  $("#addrowbutton").on('click', function () {
                	  $("#userId4Add").show();
                	  $("#userId4Update").hide();
                	  openUserInfoPopup("add",null);
                  });
                  // delete row.
                  $("#deleterowbutton").on('click', function () {
                	  var rowData = getSelectedRowData();
                	  if(rowData) {
                		  if(confirm("선택한 사용자를 삭제하시겠습니까?")){
                			  $("#userId").val(rowData.userid);
                			  $("#actionType").val("del"); 
                			  doActionUserInfo();
                          }
                  	  }                       
                  });
              },
              columns: [
                { text: '아이디', datafield: 'userid', width: 100},
                { text: '패스워드', datafield: 'password', width: 100},
                { text: '사용자명', datafield: 'username', width: 100},
                { text: '권한', datafield: 'rolename', width: 150},
                { text: '이메일', datafield: 'email'}	                 
            ]
          });

          /*사용자 등록 및 수정 팝업 */
          $('#userInfoWindow').jqxWindow({  
              width: 400,
              height: 250, resizable: false,                
              autoOpen:false,
              isModal : true,
              cancelButton: $('#btnCancelUser'),
              initContent: function () {
                  $('#btnRegUser').jqxButton({ width: '80px'});
                  $('#btnCancelUser').jqxButton({ width: '80px'});
                  $('#btnRegUser').on("click",doActionUserInfo);

              }
          });
          
          $("#userId").jqxInput({placeHolder: "아이디를 입력하세요", height: 25, width: 200, minLength: 1});
          $("#userName").jqxInput({placeHolder: "사용자명를 입력하세요", height: 25, width: 200, minLength: 1});
          $("#passWd").jqxInput({placeHolder: "패스워드를 입력하세요", height: 25, width: 200, minLength: 1});
          $("#userEmail").jqxInput({placeHolder: "사용자 이메일을 입력하세요", height: 25, width: 200, minLength: 1});
          var authRoleSource =
          {
              datatype: "json",
              datafields: [
                  { name : 'roleid'},
                  { name : 'rolename'}
              ],
              url: "/role/list.do"
          }   
			var authRoleDataAdapter= new $.jqx.dataAdapter(authRoleSource);
          $("#userRole").jqxDropDownList({ source: authRoleDataAdapter, displayMember: "rolename", valueMember: "roleid", autoDropDownHeight:true, width: '120px', height: '25px'});

   		  
   		        		  
	  });
		function getSelectedRowData(){
			var selectedIndex = $('#userMgrGrid').jqxGrid('getselectedrowindex');
			if(selectedIndex >= 0) {
				var selectedRowData = $('#userMgrGrid').jqxGrid('getrowdata', selectedIndex);
				return selectedRowData;
			} else {
				alert("선택된 항목이 없습니다.");
				return "";
			}
		}
		function openUserInfoPopup(actionType,rowData){
			if(actionType == "add"){
				$("#userInfoWindow").jqxWindow('setTitle',"사용자 추가")
				$("#userRole").jqxDropDownList("selectIndex",0);
				
				$("#frmUserInfo")[0].reset();
				
			} else {
				$("#userInfoWindow").jqxWindow('setTitle',"사용자 수정")
				$("#userIdDis").html("<b>"+rowData.userid+"</b>");
				$("#userId").val(rowData.userid);
				$("#passWd").val(rowData.password);
				$("#userName").val(rowData.username);				
				$("#userEmail").val(rowData.email);
				$("#userRole").jqxDropDownList("val",rowData.roleid);				
			}
			$("#actionType").val(actionType); 
			$("#userInfoWindow").jqxWindow('open');	
		}

		function doActionUserInfo(){
			var actionType = $("#actionType").val(); 
			var actionUrl = "";
			if(actionType=="add"){
				actionUrl = "/user/insert.do";
			} else if(actionType=="update"){
				actionUrl = "/user/update.do";
			} else if(actionType=="del"){
				actionUrl = "/user/delete.do";
			}	
			var paramData = {
				userid  :  $("#userId").val(),
				password :$("#passWd").val(),
				email : $("#userEmail").val(),
				username : $("#userName").val(),
				roleid :$("#userRole").jqxDropDownList("val")
			};
			$.post(actionUrl,paramData,function(data){
				alert(data.message);
				if(data.result) {
					$("#userInfoWindow").jqxWindow('close');
					$("#userMgrGrid").jqxGrid('updatebounddata');
					$("#userMgrGrid").jqxGrid('clearselection');
				} 

			},"json");
		}
  </script>

<div id="userMgrGrid">
	
</div>
<div id="userInfoWindow">
	<div>사용자 관리</div>
	<div>
		<form id="frmUserInfo" >
			<input type="hidden" id="actionType" name="actionType"/>
		    <div id="userId4Update" style="display:none;"> <div class="configLabel"><label>아이디: </label></div><div style="float:left;"><div id="userIdDis" style="font-size:16px;line-height:30px;"></div></div></div>
		    <div id="userId4Add" > <div class="configLabel" ><label>아이디: </label></div><div style="float:left;"><input type = "text" id="userId" name="userId"></div></div>
		    <br>
		    <div class="configLabel"><label>패스워드: </label></div><div style="float:left;"><input type="text" id="passWd" name="passWd"/></div><br>
		    <div class="configLabel"><label>사용자명: </label></div><div style="float:left;"><input type="text" id="userName" name="userName"/></div><br>
		    <div class="configLabel"><label>이메일: </label></div><div style="float:left;"><input type="text" id="userEmail" name="userEmail"/></div><br>
		    <div class="configLabel"><label>권한: </label></div><div style="float:left;"><div id="userRole"></div></div><br>
		</form>
		<div style="clear:both;padding:30px 10px 10px 10px;text-align:center;"><input type="button" value="저장" style="margin-bottom: 5px;" id="btnRegUser" /><input type="button" value="닫기" id="btnCancelUser" /></div>
	</div>
	
</div>