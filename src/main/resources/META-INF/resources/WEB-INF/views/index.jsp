<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<sec:authorize access="hasRole('ROLE_ADMIN')" var="hasRoleAdmin"></sec:authorize>
<sec:authorize access="hasRole('ROLE_USER')" var="hasRoleUser"></sec:authorize>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta name="description" content="THUMBNAIL EXTRACTOR" />
    <title id='Description'>AMUZLAB EXTRACTOR NMS</title>
    <link href="css/jqx.base.css" rel="stylesheet" type="text/css" />
    <link href="css/jqx.black.css" rel="stylesheet" type="text/css" />
    <link href="css/main.css" rel="stylesheet" type="text/css" />
    <link href="css/progress.css" rel="stylesheet" type="text/css" />
    <script src="js/rainbow.min.js"></script>
	<script type="text/javascript" src="js/lib/jquery-1.11.1.min.js"></script>
	<script type="text/javascript" src="js/lib/socket.io-1.4.5.js"></script>	
	<script src="js/lib/jqx-all.js"></script>
	<script src="js/utils.js"></script>	
	<script src="js/serverMgr.js"></script>
	<script src="js/failover.js"></script>			
	<script src="js/jquery-asPieProgress.min.js"></script>	
	<script src="js/amuzlab/ems/ems.js"></script>
	<script src="js/amuzlab/nms/nms.js"></script>
	<script src="js/lib/ion.sound.min.js"></script>
	<script src="js/ajaxSetup.js"></script>
	
    <script type="text/javascript">
		$.jqx.theme = 'black';
		
		
		var isFirstLoaded = true;
		// var socket = io.connect(location.origin);
		var socket; 
		var curServerInfo;
		var alarmSeverList = {};
		var logFromStorage = [];
		
		var socketIoStatus = false;
		var preSnmpStatus = {};
		var alarmMuteStatus = {};
		var isAlarmRun = false;
		(function socketConnect(){
			nms.sync.getClientSocketServer(function(result){
				if(result.result){
					socket = io.connect('http://' + result.socketServerIp + ':' + result.socketServerPort);
					socket.on('connect',function(data){ 
						socketIoStatus = true;
						$("#socketLoader").jqxLoader("close");
					});
					socket.on('connect_error',function(data){ 
					
						if(socketIoStatus){
							alert("NMS 서버와의 연결이 끊어졌습니다. 재접속을 시도합니다.");
							$("#socketLoader").jqxLoader("open");
						}
						socketIoStatus = false;
					});

					socket.on('reconnect',function(data){ 
						$("#socketLoader").jqxLoader("close");
						document.location.reload();
						socketIoStatus = true;
					});
					
				}else{
					console.log("socketConnect")
					socketConnect();
				}
			}, function(error){				
				socketConnect();
			});
		})();

		//getlocatStorageData();			
	
		var ATE_SVR_LIST = [];
		var ATE_GRP_LIST = [];
		var nmsLogSource;
		var nicStatusList = {};
		var nicListByBonding = {
			//'nicInput' : ['ens7f0', 'ens7f1' , 'ens8f0' , 'ens8f1'],
			//'nicOutput' :['ens6f0' , 'ens6f1'],
			//'nicManage' : ['eno49' , 'eno50']
			'nicInput' : [5,6,7,8],
			'nicOutput' :[9,10],
			'nicManage' : [1,2]
			//'nicInput' : [0,1,2,3],
			//'nicOutput' :[0,1],
			//'nicManage' : [0,1]
		};
		
        $(document).ready(function () {
        	$('#dataLoader').jqxLoader('open');
        	$('#mainSplitter').jqxSplitter({ width: "100%", height: "100%", orientation: 'horizontal', panels: [{ size: '70%' }, { size: "30%" ,min:100}] });

        	initRegPopup();			
	    	initRegTypeList();
	    	//initUseYnList();
	    	/*서버 그룹 드롭다운 리스트 초기화*/
	    	getServerGroupList();	    	
	    	initLogPopup();	
	    	initUserPopup();
			initErrorAlarm();
			
			/*DB에서 서버 리스트 가져오기*/
			getServerDataList();
        	/*로그 그리드 생성*/
        	createLogGrid();

        	/*화면 구성*/
        	initContantsLayout();
        	
    		
    		
    		//var jsonObject = {id: "getdream",
            //        message: "test"};
  			//socket.emit('chatevent', jsonObject);  			
			
	    	
	    	$("#dataLoader").jqxLoader({ isModal: true, width: 100, height: 60 });
	    	$("#socketLoader").jqxLoader({ isModal: true, width: 100, height: 60,text:'연결 시도중...'});
	    	
			$("#svrGrpName").jqxInput({placeHolder: "서버 그룹명을 입력하세요", height: 25, width: 150, minLength: 1});
			$("#svrName").jqxInput({placeHolder: "서버명을 입력하세요", height: 25, width: 150, minLength: 1});
			//$("#macAddr").jqxMaskedInput({ width: 150, height: 25, mask: 'AA:AA:AA:AA:AA:AA',promptChar:" "});
			$("#ipAddr").jqxInput({ width: 150, height: 25, placeHolder:"서버주소를 입력하세요"});
			$("#svrPort").jqxNumberInput({ width: 150, height: 25, placeHolder:"서버포트를 입력하세요",decimalDigits:0,digits:5,inputMode:'simple'});
       	    $("#btnOpenRegPopup").on("click",openRegPopup);
       	 	$("#btnOpenLogPopup").on("click",openNmsLogPopup); 
       	    $("#btnOpenUserPopup").on("click",openUserMgrPopup); 
       	 
       		$('#btnLogout').jqxButton({ width: '80px'});
       		$("#btnLogout").on("click",doLogout);
       	 	$("#searchDate").jqxDateTimeInput({ width: 250, height: 25,  formatString: "yyyy-MM-dd",selectionMode: 'range' });
       	 	initSearchLogTypeList();
       	 	/*NMS 로그 팝업 그리드 생성*/
	    	initLogGridPopup()
	    	
			$('#regTypeList').on('select', function (event)
       			{
       			    var args = event.args;
       			    if (args) {
       			    // index represents the item's index.                
       			    var index = args.index;
       			    var item = args.item;
       			    // get item's label and value.
       			    var label = item.label;
       			    var value = item.value;
       			    var type = args.type; // keyboard, mouse or null depending on how the item was selected.
   			 	
       			    if(value == "GRP"){
       			    	$('#regServerWindow').jqxWindow({height: 180,position:"center"});
       			    	$("#svrRegForm").hide();
       			    	$("#svrGrpRegForm").show();
       			    	
       			    	
       			    } else {
       			    	$('#regServerWindow').jqxWindow({height: 280,position:"center"});
       			    	$("#svrGrpRegForm").hide();
       			    	$("#svrRegForm").show();
       			    }
       			}                        
			});
			
						
			$('#btnRegServer').on("click",function(){
				var item = $("#regTypeList").jqxDropDownList('getSelectedItem'); 
				var cnfmMessage = "";
				var actionType = $("#actionType").val();
				if(actionType == "add") {
					if(item.value == "GRP") {
						cnfmMessage = "서버 그룹을 추가하시겠습니까?";
					} else {
						cnfmMessage = "ATE 서버를 추가하시겠습니까?";
					}
				} else {
					if(item.value == "GRP") {
						cnfmMessage = "그룹 정보를 수정하시겠습니까?";
					} else {
						cnfmMessage = "ATE 서버정보를 수정하시겠습니까?";
					}
				}
				if(confirmWindow(cnfmMessage,'${hasRoleAdmin}')) {
					saveServerInfo(actionType);
				}
			});
			/* 알람 사운드 상태 표시 */
			setAlarmSoundStatus();			
			//ion.sound.play("error_alarm");
			$(".alarmSound").on("click",function(){
				localStorage.setItem("alarmSoundStatus", ($(".alarmSound").hasClass("on"))?"off":"on");
				setAlarmSoundStatus();
				controlAlarmSound();
			});
			
			$('#nicStatusWindow').jqxWindow({  width: 400,
                height: 300, resizable: false,                
                autoOpen:false,
                cancelButton: $('#btnCancelNic'),
                initContent: function () {
                    $('#btnCancelNic').jqxButton({ width: '80px'});

                }
            });

			checkAteConnection();

			//getActiveAteLogData();	
			initSocketEventBind();		
			$('#dataLoader').jqxLoader('close');

        });      

        function checkAteConnection(){
            var tryCount = ATE_SVR_LIST.length;
            //배열 복사
           // var availSvrList = $.parseJSON(JSON.stringify(ATE_SVR_LIST)); 
            var availSvrList = ATE_SVR_LIST.slice(0); 

			for(var i = ATE_SVR_LIST.length - 1;i >= 0;i--) {
				var serverInfo = ATE_SVR_LIST[i];
				if(serverInfo.server_ip != ""){				
					ems.async.getActiveLog({
		        		ip: serverInfo.server_ip,
		        		port: serverInfo.server_port,
		        		timeout : 2000
		        	}, function(result){
		        		
		        		tryCount--;	 
		        		if(tryCount == 0) {
		        			getActiveAteLogData(availSvrList);
				        }       		
		        		
		        	}, function(xhr, status, err){
		        		//logFromStorage = $.merge(logFromStorage,[]);
       					tryCount--;
		        		availSvrList.splice(tryCount, 1);				        		        		
		        		if(tryCount == 0) {
		        			getActiveAteLogData(availSvrList);
				        }
		        			
		        	});
				}
			}
			
		}
			
        function getServerDataList() {
        	nms.sync.getServerDataList(function(data){
    			if(data){
    				ATE_SVR_LIST = data.result;
    			}else{
    				console.error("DB로부터 서버 리스트 가져오기를 실패했습니다.");
    				return;
    				//로그
    			}
    		}, function(err){
    			console.error('getSocketServer error : ', err);    			
				return;
    		});            
        }
        function saveServerInfo(actionType){
        	var actionURL = "";
        	var paramData = {};
        	var item = $("#regTypeList").jqxDropDownList('getSelectedItem'); 
        	var logInfo = "";
        	var targetId = "0";
        	if(actionType == "add") {      		
				var cnfmMessage = "";
				if(item.value == "GRP") {
					actionURL = "/admin/group/insert.do";
					paramData = {
	        				group_name : $("#svrGrpName").val()
	        		};
					
					logInfo = "서버 그룹["+$("#svrGrpName").val()+"]";
				} else {
					actionURL = "/admin/server/insert.do";
					paramData = {							
	        				server_name : $("#svrName").val(),
	        				server_ip : $("#ipAddr").val(),
	        				server_port : $("#svrPort").val(),
	        				//server_mac : $("#macAddr").val(),
	        				status : 0,
	        				grp_id : $("#svrGrpList").val(),
	        				server_type : $("#serverType").val(),
	        				fail_over : $("#serverType").val(),
	        				mode : $("#serverType").val()
	        		}
					
					logInfo = "서버["+$("#svrName").val()+"]";
				}
        		
        	} else {
        		if(item.value == "GRP") {
					actionURL = "/admin/group/update.do";
					paramData = {
							id : $("#sid").val(),
	        				group_name : $("#svrGrpName").val()
	        		};
					logInfo = "그룹["+$("#svrGrpName").val()+"]";
				} else {
					actionURL = "/admin/server/update.do";
					paramData = {
							id : $("#sid").val(),
	        				server_name : $("#svrName").val(),
	        				server_ip : $("#ipAddr").val(),
	        				server_port : $("#svrPort").val(),	        				
	        				//server_mac : $("#macAddr").val(),
	        				//status : $("#useYn").val(),
	        				grp_id : $("#svrGrpList").val()
	        		}
					
					logInfo = "서버["+$("#svrName").val()+"]";
				}
        		targetId = $("#sid").val();
        	}

        	$.ajax({
       		   url: actionURL,
       		   dataType: "json",
       		   data : paramData,
       		   type: 'PUT',
       		   success: function(response) {
       		     if(response.result) {
       		    	 alert("정상적으로 저장되었습니다.");
       		    	$("#regServerWindow").jqxWindow('close');
       		    	initLayout();
       		    	if(actionType == "add") {  
       		    		paramData.id = response.serverId;    		
       						logInfo+="이 추가되었습니다.";
	       		     } else {
       						logInfo+="의 정보가 수정되었습니다.";       		    	
	       		     }
       		    	var logData = {
		 	     		    	server_id : targetId,
		 	     		    	message: logInfo,		
		 	     		    	info : "", 	     		    	
		 	     		    	log_type: 'I'
		 	     	 };
    		    	 saveNmsLog(logData);
    		    	 if(item.value == "SVR") {
        		    	inform2ATEServer(paramData, actionType);
        		    	restartSnmpService();
    		    	 }
       		     } else {
       		    	alert("저장중 오류가 발생했습니다.");
       		    	var errCode = "";
       		    	if(actionType == "add") {      		
	   						logInfo+=" 추가중 오류";
	   						errCode = "E501";
	       		     } else {
	   						logInfo+=" 정보 수정중 오류";
	   						errCode = "E502";    		    	
	       		     }
       		    	var logData = {
		 	     		    	server_id : targetId,
		 	     		    	message : logInfo,
		 	     		    	log_type: 'E',
		        				code : errCode
		 	     		};
    		    	 saveNmsLog(logData);
       		     }
       		   },
       		   error : function(xhr, status, err){
       			if(xhr.status != 403) {
					if(actionType == "add") {      		
						logInfo+=" 추가중 오류";
					} else {
						logInfo+=" 정보 수정중 오류";       		    	
					}
	       			var logData = {
		 	     		    	server_id : targetId,
		 	     		    	message:logInfo,
		 	     		    	info: "NMS 서버의 연결에 문제발생",
		 	     		    	log_type: 'E',
		        				code : "E702"
		 	     		};
			    	 saveNmsLog(logData);
       		   		}
       		   }
       		});
        }

        function inform2ATEServer(serverInfo, type){
        	var paramData;
        	switch(type){
        	case 'add':
        	case 'mod':
        		nms.sync.getSocketServer(function(result){
        			if(result.result){
   						paramData= {
   							server_ip: result.socketServerIp,   							
   							server_port: result.socketServerPort,
   							server_id: serverInfo.id
   						};
        			}else{
        				alert('NMS의 소켓 서버 정보를 가져오는 것을 실패했습니다.');
        				return;
        				//로그
        			}
        		}, function(err){
        			console.error('getSocketServer error : ', err);
        			alert('NMS의 소켓 서버 정보를 가져오는 것을 실패했습니다.');
    				return;
        		});
        		break;        	
        	default:
        		paramData = {
        			server_port: null,
					server_ip: null
        		};
        	}
        	
        	ems.async.sendNMSSocketServerInfo({
        		ip: serverInfo.server_ip,
        		port: serverInfo.server_port
        	}, function(result){
        		if(result.result){
        			changeServerMode(serverInfo.id, (result.mode)?"1":"0");        			
        		}else{
        			saveNmsLog({
        				server_id: serverInfo.id,
        				message: 'ATE[' + serverInfo.server_name + ']에 NMS 소켓 서버 정보를 전달하는 것을 실패했습니다 (오류 정보  : ' + result.err.message ? result.err.message : result.err + ')',
        				info: serverInfo.server_ip+":"+serverInfo.server_port,	
                		log_type: 'E',
	        			code : "E507"//, //외부 연동 오류
	        			//clear_date : (new Date).getFromFormat('yyyy-mm-dd hh:ii:ss')
        			})
        			alert('ATE에 NMS 소켓 서버 정보를 전달하는 것을 실패했습니다');
        			
        		}
        	}, function(err){
        		saveNmsLog({
					server_id : serverInfo.id,
					message: 'NMS 정보를 ATE['+serverInfo.server_name+']로 전송 중 오류 발생(오류 정보 : ' + err.message + ')',
					info: serverInfo.server_ip+":"+serverInfo.server_port,
					log_type: 'E',
        			code : "E701"
				});
        	}, paramData);
        }
        function output(message) {
            var currentTime = "<span class='time'>" + new Date() + "</span>";
            var element = $("<div>" + currentTime + " " + message + "</div>");
			$('#mainContainer').prepend(element);
		}
        
        function createLogGrid(){
        	var logData = [];
        	var errorLogSource =
            {
                datatype: "json",
                datafields: [
                 { name: 'id', type: 'string' },
                 { name: 'code', type: 'string' },
                 { name: 'create_date', type: 'string' },
                 { name: 'code', type: 'string' },
                 //{ name: 'sid', type: 'string' },
                 { name: 'log_type', type: 'string' },
                // { name: 'confirm', type: 'bool' },
                 { name: 'info', type: 'string' },
                 { name: 'message', type: 'string' },
                 { name: 'serverName', type: 'string' },
                 { name: 'server_id', type: 'string' }
                    
                ],
                id: 'id',
	            localdata: logFromStorage
	               
            };
            var errorLogDA = new $.jqx.dataAdapter(errorLogSource);
 			
            $("#logContainer").jqxGrid(
            {		                  
          	  	width:'100%',
          	  	height: '100%',
	            filterable: true,				              
          	    source: errorLogDA,
                columnsresize: true,
                sortable: true,
                editable: true,
                columns: [
					//{ text: '종류', datafield: 'type', filter: addErrorGridFilter,hidden:true},
                  //{ text: '확인', width: 70, cellsalign: 'center' ,columntype: 'checkbox'},
                  { text: '서버', datafield: 'serverName', width: 150 ,editable: false,cellsrenderer: serverNameRenderer},
	                 { text: '날짜', datafield: 'create_date', width: 150 ,editable: false},
	                 //{ text: '채널명', datafield: 'channelName', width: 150 ,editable: false},
	                 //{ text: 'SID', datafield: 'sid', width: 80 ,editable: false},
	                 { text: '코드', datafield: 'code', width: 100 ,editable: false},
	                 { text: '내용', datafield: 'info', cellsrenderer:logrenderer ,editable: false}
	                 
              ]
            });

			
        }
        function logrenderer(row, columnfield, value, defaulthtml, columnproperties,rowData) {
            if(rowData) {
				return '<div style="text-align: center;margin-top: 5px;">' + rowData.message+((value)?"("+value+")" : "")+'</div>';
            } else {
				return value;
            }
		}
        function serverNameRenderer(row, columnfield, value, defaulthtml, columnproperties,rowData) {            
            var serverInfo = getServerDataById(rowData.server_id);
            if(serverInfo === null || serverInfo === undefined) {
            	//return "<div style='line-height: 30px;padding-left:3px;'>삭제된 서버"+rowData.server_id+"</div>";
            	return "<div style='line-height: 30px;padding-left:3px;'>삭제된 서버</div>";
            }
            return "<div style='line-height: 30px;padding-left:3px;'>"+serverInfo.server_name+"</div>";
        }
        function initLogGridPopup(){
        	nmsLogSource =
            {
                datatype: "json",
                datafields: [
                 { name: 'id', type: 'string' },
                 { name: 'server_id', type: 'string' },
                 { name: 'server_name', type: 'string' },
                 { name: 'message', type: 'string' },
                 { name: 'info', type: 'string' },
                 { name: 'create_date', type: 'string' },
                 { name: 'clear_date', type: 'string' },
                 { name: 'log_type', type: 'string' }                    
                ],
                id: 'id',
                url: "/nmslog/list.do",
                root : "datas",
                /*
                data: {
                	search_start : $("#searchDate").jqxDateTimeInput('getRange').from.getFromFormat('yyyy-mm-dd hh:ii:ss'),
                	search_end : $("#searchDate").jqxDateTimeInput('getRange').to.getFromFormat('yyyy-mm-dd hh:ii:ss')
                }
                */
                formatdata:function(data){                  	
                	data.search_start = $("#searchDate").jqxDateTimeInput('getRange').from.getFromFormat('yyyy-mm-dd hh:ii:ss');
                	data.search_end = $("#searchDate").jqxDateTimeInput('getRange').to.getFromFormat('yyyy-mm-dd hh:ii:ss');
                	data.log_type = $("#searchLogType").jqxDropDownList("val");
                	if(curServerInfo) data.server_name = curServerInfo.server_name;
                	return data;
                },
                beforeprocessing: function(data) {
                    if (data != null && data.length > 0) {
                    	nmsLogSource.totalrecords = data.logs.totalRecords;
                    }
                },
                sort: function() {
                    // update the grid and send a request to the server.
                    $("#nmsLogGrid").jqxGrid('updatebounddata', 'sort');
                },
	               
            };
            var nmsLogDA = new $.jqx.dataAdapter(nmsLogSource);
 			
            $("#nmsLogGrid").jqxGrid(
            {		                  
          	  	width:'100%',
          	  	height: '80%',
	            filterable: true,				              
          	    source: nmsLogDA,
                columnsresize: true,
                sortable: true,
                editable: false,
                pageable: true,
                pagermode: 'simple',
                virtualmode: true,
                rendergridrows: function(obj) {
                    return obj.data;
                },
                columns: [
                  { text: '서버', datafield: 'server_name', width: 100},
                  { text: '생성날짜', datafield: 'create_date', width: 150},
                  { text: '복구날짜', datafield: 'clear_date', width: 150},
                  { text: '종류', datafield: 'log_type', width: 100},
                  { text: '내용', datafield: 'info', width: 'auto', cellsrenderer:logrenderer},
   
              ]
            });
            
            var getSearchCondition = function(){
				var paramData = {
						search_start : $("#searchDate").jqxDateTimeInput('getRange').from.getFromFormat('yyyy-mm-dd hh:ii:ss'),
		        		search_end : $("#searchDate").jqxDateTimeInput('getRange').to.getFromFormat('yyyy-mm-dd hh:ii:ss')
					};
				return paramData
			}
        }
        
        function initRegTypeList(){
        	"Server그룹 등록",
            "ATE Server 등록"
        	var source = [
        	              {
        	                  "name": "Server그룹 등록",
        	                  "value": "GRP",
         	              },
        	              {
           	                  "name": "ATE Server 등록",
        	                  "value": "SVR"
        	              }
        	      ];
        	 var dataAdapter = new $.jqx.dataAdapter(source);
			$("#regTypeList").jqxDropDownList({ 
				source: dataAdapter, 
				displayMember: "name", 
				valueMember: "value",
				autoDropDownHeight: true,
				placeHolder :"등록 대상을 선택해주세요.", width: '200', height: '25'});     
			
			
        }

        function initSearchLogTypeList() {
        	var source = [
						{
						    "name": "모두보기",
						    "value": "",
						 },
        	              {
        	                  "name": "오류로그",
        	                  "value": "E",
         	              },
        	              {
           	                  "name": "일반로그",
        	                  "value": "I"
        	              }
        	      ];
        	 var dataAdapter = new $.jqx.dataAdapter(source);
        	$("#searchLogType").jqxDropDownList({ 
				source: dataAdapter, 
				displayMember: "name", 
				valueMember: "value",
				autoDropDownHeight: true,
				placeHolder :"로그종류", width: '200', height: '25'});     

        }
        function initUseYnList(){
        	var source = [
                          {value : 1, name : "사용"},
                          {value : 0, name : "사용안함"}
                          
      		        ];
        	var dataAdapter = new $.jqx.dataAdapter(source);
			$("#useYn").jqxDropDownList({ source: dataAdapter, displayMember: "name", 
				valueMember: "value",
				autoDropDownHeight: true,selectedIndex: 0, width: '120', height: '25'});       	
        }
        
        function initRegPopup(){
        	$('#regServerWindow').jqxWindow({  width: 400,
                height: 150, resizable: false,                
                autoOpen:false,
                isModal:true,
                cancelButton: $('#btnCancelReg'),
                initContent: function () {
                    $('#btnRegServer').jqxButton({ width: '80px'});
                    $('#btnCancelReg').jqxButton({ width: '80px'});

                }
            });
        	
        	$('#regServerWindow').on('close', function (event) {                
                $("form")[0].reset();
                $("#svrGrpList").jqxDropDownList('selectIndex', 0 ); 
//                $("#useYn").jqxDropDownList('selectIndex', 0 ); 
                $("#regTypeList").jqxDropDownList("disabled",false); 
 		    	$("#svrGrpList").jqxDropDownList("disabled",false); 
   
            });
        }
        
        function initLogPopup(){        	
        	$('#nmsLogWindow').jqxWindow({  minWidth:1200, maxWidth: 1600, minHeight: 450, maxHeight: 900,
                height: 500, resizable: true,isModal :true,           
                autoOpen:false
            });

        	$('#nmsLogWindow').on('close', function (event) {
        		$('#nmsLogGrid').jqxGrid("clear");
        		$("#searchLogType").jqxDropDownList('val', '');
        		$('#nmsLogGrid').jqxGrid('gotopage', 0);
        	});
        	
        	
        	$("#btnSearchLog").jqxButton({ width: '80px'});
        	$("#btnSearchLog").on("click",searchNmslog);
        }

        function initUserPopup(){        	
        	$('#userMgrWindow').jqxWindow({  width: 800,
                height: 450, resizable: true,                
                autoOpen:false,
                isModal : true
            });
        	//$("#btnSearchLog").jqxButton({ width: '80px'});
        	//$("#btnSearchLog").on("click",searchNmslog);
        }
        
        function searchNmslog(){
            var logUrl = "";
            if($("#popupType").val() == "NMS") {
            	logUrl = "/nmslog/list.do";
            } else {
            	logUrl = "http://"+curServerInfo.server_ip+":"+curServerInfo.server_port+"/logData";
            }
            var gridSource = $("#nmsLogGrid").jqxGrid('source');
            gridSource._source.url = logUrl;
            $("#nmsLogGrid").jqxGrid('source', gridSource);            
//        	$("#nmsLogGrid").jqxGrid('updatebounddata');
        }
        function openRegPopup(){
        	$("#actionType").val("add");
        	$("#regServerTitle").html("서버 그룹 등록");
        	$("#regTypeList").jqxDropDownList("val","GRP");
        	$("#regTypeList").jqxDropDownList({disabled:true});
        	$("#regServerWindow").jqxWindow('open');
        }
        function openNmsLogPopup(){        	
        	$("#nmsLogWindow").jqxWindow('setTitle',"NMS 로그")        	
            $("#popupType").val("NMS");
        	searchNmslog();        	
        	curServerInfo = null;
        	$("#nmsLogWindow").jqxWindow('open');

        	setTimeout(function(){
				$('#nmsLogGrid').jqxGrid('hidecolumn', 'message');
				$('#nmsLogGrid').jqxGrid('showcolumn', 'server_name');
			},300);	
			
        }

        function openUserMgrPopup(){
        	$("#userMgrContainer").load("/user.do",function(page){
            	$("#userMgrWindow").jqxWindow('open');
            });
        	
        }
        
        
        function getServerGroupList(){          

        	nms.sync.getServerGroupDataList(function(data){
    			if(data){
    				ATE_GRP_LIST = data.result;
    				initServerGroupList();
    			}else{
    				console.error("DB로부터 서버 그룹 리스트 가져오기를 실패했습니다.");
    				return;
    				//로그
    			}
    		}, function(err){
    			console.error('getSocketServer error : ', err);    			
				return;
    		});        	
        }


        function initServerGroupList() {

        	var svrGroupListSource =
            {
                datatype: "json",
                datafields: [
                 { name: 'id', type: 'string' },
                 { name: 'group_name', type: 'string' }                    
                ],
                id: 'id',
	            localdata: ATE_GRP_LIST
	               
            };
            
        	var dataAdapter = new $.jqx.dataAdapter(svrGroupListSource);
        	if(isFirstLoaded) {
				$("#svrGrpList").jqxDropDownList({ 
					source: dataAdapter, 
					displayMember: "group_name", 
					valueMember: "id",
					autoDropDownHeight: true,selectedIndex: 0, width: '120', height: '25'
				});
        	} else {
        		$("#svrGrpList").jqxDropDownList({source: dataAdapter});
        	}
        	
			isFirstLoaded = false;
        }
        function initContantsLayout(){
        	var grpElement = "";
        	var svrElement = "";
        	var grpId = "";
        	var grpName = "";
        	var serverStatus = "";
        	var buttonStatus = "";
        	//ATE_SVR_LIST = [];
        	
        	for(var i = 0;i < ATE_GRP_LIST.length;i++){
            	var data = ATE_GRP_LIST[i];
        		grpId = data.id;
        		grpName = data.group_name;      
        	        		  		
        		grpElement = '<fieldset style="float:left;position: relative;" class="srvGrpContainer" id="srvGrpWrap_'+grpId+'">';
        		grpElement+= '<legend class="svrGrpLegend" data-value="'+grpId+'">'+grpName+'</legend>';
        		grpElement+= '<div style="position: absolute;right: 20px;top: 20px;"><div style="float:left;line-height:30px;" id="bindStatusInfo_'+grpId+'"></div><div class="bindStatusImg" id="bindStatusImg_'+grpId+'"></div><div style="float:left;" data-value="'+grpId+'"><input class="btnBindServer" id="btnBindServer_'+grpId+'" /></div></div>';
        		grpElement+= '<div class="MAIN_Box"><a href="#" class="addServerBox" data-value="MAIN" data-key="'+grpId+'"><div class="horizontalCross"></div><div class="verticalCross"></div><div class="addServerTitle">MAIN 서버 등록</div></a></div>';
        		grpElement+= '<div class="BACK_Box"><a href="#" class="addServerBox" data-value="BACK" data-key="'+grpId+'"><div class="horizontalCross"></div><div class="verticalCross"></div><div class="addServerTitle">BACKUP 서버 등록</div></a></div>';
        		      		
        		grpElement+= '</fieldset>';    			
    			$("#contentContainer").append(grpElement);
    			
    			var serverList = getServerListByGroup(grpId);
    			//$.ajaxSetup({async:false})
        		//$.get("/server/list.do",
        		//	{
        		//		id : grpId,
        		//		group_name : grpName
        		//	},function(svrData){
        				if(serverList.length > 0) {
		        			for(var j = 0;j < serverList.length;j++){
			        			var dt = serverList[j];
								//console.log("ATE_SVR_LIST.push(dt)");
		        				//ATE_SVR_LIST.push(dt);
		        				serverStatus = "stop";
		        				if(dt.mode == 1) {
		        					serverStatus += " run";
		        					buttonStatus = "stop";
		        				} else {
		        					buttonStatus = "run";
		        				}

		        				if(dt.status == 1 || dt.snmp_status == 1) {
		        					serverStatus += " error";
		        				} 
		        				
		        				svrElement = "";
		        				svrElement+='<fieldset style="float:left;width:80%" class="svrInfoWrap">';
		        				svrElement+='	<legend>'+dt.server_name+'</legend>';
		        				svrElement+='	<div style="float:left;" id = "serverImg_'+dt.id+'" data-value="'+dt.id+'" class="serverImgBox '+serverStatus+'"></div>';
		        				 
		        				svrElement+='	<div style="float:left;padding-left:20px;">';
		        				svrElement+='	   <div style="float:left;width:50%;">';
		        				svrElement+='	   <div>라이센스 : <span id="licenceCnt_'+dt.id+'" class="serverInfo-text"></span></div>';
		        				svrElement+='	   <div>전체 ATE : <span id="allAteCnt_'+dt.id+'" class="serverInfo-text"></span></div>';
		        				svrElement+='	   <div>실행중 ATE : <span id="runAteCnt_'+dt.id+'" class="serverInfo-text"></span></div>';
		        				svrElement+='	   <div>중지 ATE : <span id="stopAteCnt_'+dt.id+'" class="serverInfo-text"></span></div>';
		        				svrElement+='	   </div>';
		        				svrElement+='	   <div style="float:left">';
		        				svrElement+='	   <div>Error ATE : <span id="errAteCnt_'+dt.id+'" class="serverInfo-text"></span></div>';
		        				svrElement+='	   <div>Error FTP : <span id="errFtpCnt_'+dt.id+'" class="serverInfo-text"></span></div>';
		        				svrElement+='	   </div>';
		        				svrElement+='	   <div style="clear:both"></div>';
		        				svrElement+='		<div style="padding:5px;">';
		        				//svrElement+='	   <span><div id="memUsage_'+dt.id+'" chart-type="donut" data-chart-max="100" data-chart-segments="{ \'0\':[\'0\',\'30\',\'#19A7F5\'],  \'1\':[\'30\',\'70\',\'#ecebeb\'] }" data-chart-text="30%" data-chart-caption="MEM"  data-chart-initial-rotate="180"></div></span>';
		        				//svrElement+='	   <span id="diskUsage_'+dt.id+'" class="serverInfo-text"></span></div>';
		        				svrElement+='		<div id="memUsage_'+dt.id+'" class="pie_progress" role="progressbar" data-barcolor="#3daf2c" data-barsize="10" aria-valuemin="0" aria-valuemax="100">';
		        				svrElement+='    		<div class="pie_progress__number">0%</div>';
		        				svrElement+='    		<div class="pie_progress__label">MEM</div>';
		        				svrElement+='		</div>';
		        				svrElement+='		<div id="cpuUsage_'+dt.id+'" class="pie_progress" role="progressbar" data-goal="60" data-barcolor="#3daf2c" data-barsize="10" aria-valuemin="0" aria-valuemax="100">';
		        				svrElement+='    		<div class="pie_progress__number">0%</div>';
		        				svrElement+='    		<div class="pie_progress__label">CPU</div>';
		        				svrElement+='		</div>';
		        				svrElement+='		<div id="diskUsage_'+dt.id+'" class="pie_progress" role="progressbar" data-goal="60" data-barcolor="#3daf2c" data-barsize="10" aria-valuemin="0" aria-valuemax="100">';
		        				svrElement+='    		<div class="pie_progress__number">0%</div>';
		        				svrElement+='    		<div class="pie_progress__label">DISK</div>';
		        				svrElement+='		</div>';
		        				svrElement+='		<div id="nicInput_'+dt.id+'"  class="nicStatus pie_progress" role="progressbar" data-barcolor="#3daf2c" data-barsize="10" aria-valuemin="0" aria-valuemax="4096">';
		        				svrElement+='    		<div class="pie_progress__number txt12">0Mb</div>';
		        				svrElement+='    		<div class="pie_progress__label">NIC-INPUT</div>';
		        				svrElement+='		</div>';
		        				svrElement+='		<div id="nicOutput_'+dt.id+'" class="nicStatus pie_progress" role="progressbar" data-barcolor="#3daf2c" data-barsize="10" aria-valuemin="0" aria-valuemax="2048">';
		        				svrElement+='    		<div class="pie_progress__number txt12">0Mb</div>';
		        				svrElement+='    		<div class="pie_progress__label">NIC-OUTPUT</div>';
		        				svrElement+='		</div>';
		        				//svrElement+='		<div id="nicManage_'+dt.id+'" class="nicStatus pie_progress" role="progressbar" data-barcolor="#3daf2c" data-barsize="10" aria-valuemin="0" aria-valuemax="2048">';
		        				//svrElement+='    		<div class="pie_progress__number txt12">0Mb</div>';
		        				//svrElement+='    		<div class="pie_progress__label">NIC-MANAGE</div>';
		        				//svrElement+='		</div>';
		        				svrElement+='		</div>';
		        				//svrElement+='	   <div class="tiny-chartbox"><div id="memUsage_'+dt.id+'" chart-type="donut" data-chart-max="100" data-chart-segments="{ \'0\':[\'0\',\'30\',\'#19A7F5\'],  \'1\':[\'30\',\'70\',\'#ecebeb\'] }" data-chart-text="30%" data-chart-caption="MEM"  data-chart-initial-rotate="180"></div></div>';
		        				svrElement+='	</div>';
		        				svrElement+='	<div class="nicLampWrapper" >';
		        				svrElement+='		<div style="float:left;margin-right:20px;width:45%;"><div class="ifCategory">IF-IN</div>';
		        				svrElement+='			<span id="ifInputLamp_'+dt.id+'"></span>';
		        				svrElement+='		</div>';
		        				svrElement+='		<div style="float:left">';
		           				svrElement+='			<div style="margin-bottom:10px;"><div class="ifCategory">IF-OUT</div>';
		        				svrElement+='				<span id="ifOutputLamp_'+dt.id+'"></span>';
		        				svrElement+='			</div>';
		        				svrElement+='			<div><div class="ifCategory">IF-MANAGE</div>';
		        				svrElement+='				<span id="ifManageLamp_'+dt.id+'"></span>';
		        				svrElement+='			</div>';
		        				svrElement+='		</div>';
		        				svrElement+='	</div>';
		        				svrElement+='</fieldset>';
		        				svrElement+='<div style="position:relative;float:left;width: 120px;height: 120px;padding: 15px 5px 5px 15px;">';
		        				svrElement+='	<div style="float:left" class="operation info" data-value="'+dt.id+'"></div>';
		        				svrElement+='	<div style="float:left" class="operation log" data-value="'+dt.id+'"></div>';
		        				svrElement+='	<div style="clear:both;"></div>';	
		        				svrElement+='	<div style="float:left" class="operation del" data-value="'+dt.id+'"></div>';
		        				svrElement+='	<div style="float:left" id = "serverOperation_'+dt.id+'" class="operation '+buttonStatus+'"  data-value="'+dt.id+'"></div>';		        					        				
		        				svrElement+='	<div style="clear:both"></div>';
		        				svrElement+='	<div><input type="button" id = "serverRestart_'+dt.id+'" data-value="'+dt.id+'" value="서버재시작"/></div>';
		        				svrElement+='	<div id="releaseAlarmBox_'+dt.id+'" class="releaseAlarm"><input type="button" data-value="'+dt.id+'" value="알람해제"/></div>'; 
		        				svrElement+='</div>';
		        				svrElement+='<div style="clear:both;"></div>';

								var serverType = dt.server_type;
								if(!serverType){
									serverType = "M";
								}
		        				$("#srvGrpWrap_"+dt.grp_id +" ."+serverType+"_Box").html(svrElement);

		        				$("#serverImg_"+dt.id).on("click",function(){
		        					var svrId = $(this).attr("data-value");
			        				var serverInfo = getServerDataById(svrId);
			        				window.open('http://'+serverInfo.server_ip.replaceAll(' ','')+':' + serverInfo.server_port + '/nmsManager/nmsadmin','_blank');
			        			});

		        				
		        				
		        				createUsageCircle($("#memUsage_"+dt.id),100,true);
		        				createUsageCircle($("#cpuUsage_"+dt.id),100,true);
		        				createUsageCircle($("#diskUsage_"+dt.id),100,true);
		        				createUsageCircle($("#nicInput_"+dt.id)),4096,false;
		        				createUsageCircle($("#nicOutput_"+dt.id),2048,false);
		        				//createUsageCircle($("#nicManage_"+dt.id),2048,false);

		        				$("#serverRestart_"+dt.id).jqxButton({ width: '100%', height: '25'});
		        				$("#serverRestart_"+dt.id).on("click",restartServer);

		        				$("#releaseAlarmBox_"+dt.id +" input").jqxButton({ width: '100%', height: '25'});
		        				$("#releaseAlarmBox_"+dt.id +" input").on("click",function(){
		        					var svrId = $(this).attr("data-value");
		        					setAlarmMuteStatus(svrId,null,true);
		        					//for(key in alarmMuteStatus) {
		        					//	alarmMuteStatus["server_"+svrId][key] = true;
		        					//}
		        					setTimeout(function(){
			        					alarmSound(svrId)
			        				},300);
		        					//$("#releaseAlarmBox_"+svrId).hide();
		        					/*
			        				
			        				//console.log("#releaseAlarmBox_"+svrId +" 클릭")			        				
		        					$("#releaseAlarmBox_"+svrId).addClass("mute");		        					
		        					alarmSound(svrId,false);	
		        					*/	        					
		        					
			        			});
		        				dt.status = "";
		        			};

		        			$("#srvGrpWrap_"+grpId +" .operation").on("click",function(){
		        				if($(this).hasClass("del")) {
		        					delServer(this,"server");
		        				} else if($(this).hasClass("info")) {
		        					getServerInfo(this,"server");
		        				} else if($(this).hasClass("log")) {
		        					openServerLogPopup($(this).attr("data-value")); 					
		        					//runServer(this,"server");
		        				} else  {     
		        					doFailOver($(this).attr("data-value"),true); 					
		        					//runServer(this,"server");
		        				}
		                		
		                	});
        				} 

        				
        			//$("#contentContainer").append(grpElement);
        		//},"json");
    			//$.ajaxSetup({async:true})
        		$("#srvGrpWrap_"+grpId).append("<div style='position:absolute;right:5px;bottom:5px;'><button id='btnDelGroup_"+grpId+"' data-value='"+grpId+"'>그룹삭제</button></div>");
        		$("#btnDelGroup_"+grpId).jqxButton({ width: '100', height: '25'});
        		$("#btnDelGroup_"+grpId).on("click",function(){
            		delServer(this,"group");
            	});
				
        		/* 바인딩 값 세팅*/
        		var grpBindingValue = (data.binding == 1)?true:false;
        		svrGrpMgr.setBindStatus(grpId,grpBindingValue);

        		var switchBtnDisable = ('${hasRoleAdmin}' == 'true')?false:true;
        		$("#btnBindServer_"+grpId).jqxSwitchButton({width: 60, height: 25,checked:grpBindingValue,disabled : switchBtnDisable });
        		changeBindStatus(grpId,grpBindingValue);	
        		if(grpBindingValue) checkAllStandbyMode(grpId);

        	};   

        	$(".addServerBox").on("mouseover",function(){
				$(this).find(".horizontalCross").addClass("over");
				$(this).find(".verticalCross").addClass("over");
    		});

			$(".addServerBox").on("mouseleave",function(){
				$(this).find(".horizontalCross").removeClass("over");
				$(this).find(".verticalCross").removeClass("over");
    		});

			$(".addServerBox").on("click",function(){
				$("#actionType").val("add");
	        	$("#regServerTitle").html("ATE 서버 등록");
	        	$("#regTypeList").jqxDropDownList("val","SVR");
	        	$("#regTypeList").jqxDropDownList({disabled:true});
	        	$("#serverType").val($(this).attr("data-value"));

	        	$("#svrGrpList").jqxDropDownList("val",$(this).attr("data-key")); 
		    		//$("#svrGrpList").jqxDropDownList({disabled:true}); 
		    		
	        	$("#regServerWindow").jqxWindow('open');
    		});
    		     	
        	//$(".nicStatus").on("click",function(){
        	//	displayNICStatus($(this).attr("id"))
   			//});

        	
        	//createLogGrid();
        	// 그룹 바인딩 처리 
        	//$(".btnBindServer").jqxSwitchButton({ width: 60, height: 25 });
        	$('.btnBindServer').on('change', function (event) {
            	var activeCount = 0;

            	var grpId = $(this).parent().attr("data-value");
            	var svrList = getServerListByGroup($(this).parent().attr("data-value"));
            	var logInfo = "";
            	var tmpSvrInfo;

               	var backupSvrId = "";
        		for( var i = 0; i < svrList.length;i++){
        			
					if(parseInt(svrList[i].mode) == 1) {
						activeCount++;
						if(svrList[i].server_type == "BACK") {
							backupSvrId = svrList[i].id;
						}
					}
					tmpSvrInfo = getServerDataById(svrList[i].id);
					logInfo+= tmpSvrInfo.server_name;
					if(i == 0){
						logInfo+= ", "
					}
    			};
    			
				if(svrList.length < 2)  logInfo = "서버그룹 ["+$(this).parent().parent().prev().text()+"]이";
				else logInfo = "서버 ["+logInfo+"] 가";
        		svrGrpMgr.setBindStatus($(this).parent().attr("data-value"),$(this).val());
        		changeBindStatus($(this).parent().attr("data-value"),$(this).val());
        		
        		

				if($(this).val()) {
					if(activeCount > 1) doFailOver(backupSvrId,false);
					logInfo+=" 바인딩 되었습니다.";
				} else {
					logInfo+=" 바인딩 해제 되었습니다.";
				}
        		 var logData = {
	 	     		    	server_id : grpId,
	 	     		    	message: "바인딩 모드가 변경되었습니다.",
	 	     		    	info: logInfo,
	 	     		    	log_type: 'I' 	     		    	
	 	     		};
		    	  saveNmsLog(logData);
            });
        	
        	$(".svrGrpLegend").on("click",function(){
        		getServerInfo(this,"group");
        	});

        	
        }

        function restartServer(){
            var serverInfo= getServerDataById($(this).attr("data-value"))
            var serverId = serverInfo.id;           	
            $('#dataLoader').jqxLoader('open');
        	$.ajax({
     		   url: "http://"+serverInfo.server_ip+":"+serverInfo.server_port+"/restart",
     		   dataType: "json",
     		   type: 'POST',
     		   success: function(response) {
     			  var logData = {
 	     		    	server_id : serverId,
 	     		    	message: serverInfo.server_name+" 서버 재시작 성공",
 	     		    	info: serverInfo.server_ip+":"+serverInfo.server_port,
 	     		    	log_type: 'I',
 	     		    	clear_date : (new Date).getFromFormat('yyyy-mm-dd hh:ii:ss')
 	     		  };
     		     if(response.result) {
     		    	 alert("정상적으로  재시작 되었습니다.");    		    	 
     		     } else {
     		    	logData.info = serverInfo.server_name+" 서버 재시작 중 오류";
     		    	logData.log_type = "E";
         		 }	logData.code = "E508";
		    	   saveNmsLog(logData);
     		     $('#dataLoader').jqxLoader('close');
     		   },
     		   error : function(err){
     			  var logData = {
	 	     		    	server_id : serverId,
	 	     		    	message: serverInfo.server_name+" 서버 재시작 중 오류",
	 	     		    	info: serverInfo.server_ip+":"+serverInfo.server_port,
	 	     		    	log_type: 'E',	 	     		    	
		        			code : "E701"
	 	     		};
   		    	  saveNmsLog(logData);
     			  alert("서버 재시작 중 오류가 발생했습니다.");    	
     			 $('#dataLoader').jqxLoader('close');	  
     		   }
     		});
        }
        function createUsageCircle(element,maxValue,needCalc){
        	element.asPieProgress({
                namespace: 'pie_progress',
                max : maxValue,
                numberCallback:function(n) {
                    'use strict';
                    if(needCalc) {                        
                    	//percentage = Math.round(this.getPercentage(n));
                    	n+='%';
                    } else {
                    	n +='Mb';
                    }
                    return n;
                  }
            });
        }
        function initLayout(){
        	$("#contentContainer").empty();   	
	    	getServerGroupList();
	    	getServerDataList();
	    	initContantsLayout();  	
        }
        
        function delServer(element,type) {
        	var confirmMessage = "";
        	var serverId = $(element).attr("data-value");
        	var serverInfo = {};
        	var targetName = "";
        	if(type == "group") {
        		confirmMessage = "그룹을";
        		if($("#srvGrpWrap_"+serverId+" fieldset").length > 0) {
        			alert("삭제하려는 그룹에 등록된 서버가 존재하여 삭제하실수 없습니다.\n등록된 모든 서버를 삭제 후 다시 시도해주세요.");
        			return false;
        		}
        		targetName = "그룹["+$(element).parent().prev().text()+"]";
        	} else {
        		confirmMessage = "서버를";
        		serverInfo = getServerDataById(serverId);
        		targetName = "서버["+serverInfo.server_name+"]";
        	}
        	
        	if(confirmWindow("선택한 "+confirmMessage+" 삭제하시겠습니까?\n삭제 후에는 복구하실 수 없습니다.",'${hasRoleAdmin}')){
        		
        		$.ajax({
            		   url: "/admin/"+type+"/del.do",
            		   dataType: "json",
            		   data : {id : serverId},
            		   type: 'POST',
            		   success: function(response) {
            		     if(response.result) {
            		    	 alert("정상적으로 삭제되었습니다.");
            		    	 initLayout();
            		    	 var logData = {
     		 	     		    	server_id : serverId,
     		 	     		    	message: targetName+"이 삭제되었습니다.",
     		 	     		    	log_type: 'I'
     		 	     		};
      		 	     		if(type == "server") {
      		 	     			inform2ATEServer(serverInfo,"del");

								for(var i= logFromStorage.length - 1;i >=0;i--){
									if(logFromStorage[i].server_id == serverId) {
										logFromStorage.splice(i, 1);
									}
								};
								logData.info = serverInfo.server_ip+":"+serverInfo.serverInfo.server_port;
								//setlocatStorageData()
								//getlocatStorageData();
								restartSnmpService();
                  		 	} 
              		 	    
            		    	saveNmsLog(logData);
            		     } else {
            		    	 var logData = {
      		 	     		    	server_id : serverId,
      		 	     		    	message: targetName+" 삭제 중 오류",
      		 	     		    	info: "NMS 서버의 연결에 문제발생",
      		 	     		    	log_type: 'E',
      			        			code : "E503"
      		 	     		};
             		    	 saveNmsLog(logData);
            		    	alert("저장중 오류가 발생했습니다.");
            		     }
            		   },
            		   error : function(err){
            			   console.log(err);
            		   }
            		});
        	}
        }

        function changeServerStatus(serverId){
        	var serverInfo = getServerDataById(serverId);

        	if(serverInfo) {
	        	//nms.async.updateServerState(function(response) {
	        		//if(response.result) {  
	        			$("#serverImg_"+serverId).removeClass("error").removeClass("run");     
	    	
			        	if(serverInfo.mode == 1){		
			        		if(serverInfo.status == 0 && serverInfo.snmp_status == 0) {
				        		console.log("오류해제=============================="+serverId);
			        			$("#serverImg_"+serverId).addClass("run");
							} else {
								$("#serverImg_"+serverId).addClass("error");
								
							}	        	
							
							$("#serverOperation_"+serverId).removeClass("run").addClass("stop");
						} else {
							if(serverInfo.status == 1 || serverInfo.snmp_status == 1) {
			        			$("#serverImg_"+serverId).addClass("error");
							} 
							$("#serverOperation_"+serverId).removeClass("stop").addClass("run");
						}						
	  		     	}
        }
        
        function changeServerMode(serverId, modeType){     
     
        	var svrId = serverId;
        	nms.async.updateServerState(function(response) {
        		if(response.result) {      
        			$.post("/admin/reloadServerList",function(){
            		});
  		     	}
  		   }, function(err){
  			   console.log(err);
		   }, {
			   id : svrId,
			   mode : modeType
			  
		   });

        }
		function runServer(element) {
			var modeType = "";
			var returnMessage = "";			
			var serverId =$(element).attr("data-value");
			if($(element).hasClass("run")){//서버 active 상태로 변경하기
		
				var hasRunServer = false;
				$.each($(".serverImgBox"),function(idx,el){
					if($(el).hasClass("run")) {
						hasRunServer = true;
					}
				});
				
				if(hasRunServer) {
					alert("이미 실행중인 서버가 있습니다. 실행중인 서버를 중지하신 후 해당 서버를 시작해주세요.");
					return false;
				}
				modeType = "active";
				returnMessage = "시작";
			} else {//서버 standby 상태로 변경하기
				modeType = "standby";
				returnMessage = "중지";
			}
			var paramData = {
				mode : modeType
			};
			
			if(confirmWindow("서버를 "+returnMessage+"하시겠습니까?",'${hasRoleAdmin}')){		
				var serverId = $(element).attr("data-value");
				var serverInfo = getServerDataById(serverId);
				ems.async({
					ip: serverInfo.server_ip,
					port: serverInfo.server_port
				}, function(response){
					var logData;
					if(response.result) {
						alert("정상적으로 "+returnMessage+"되었습니다.");     		    	
						changeServerMode(serverId, modeType);
						logData = {
							server_id : serverId,
							message: 'ATE['+serverInfo.server_name+']를 '+modeType+'로 변경되었습니다.',
							info : serverInfo.server_ip+":"+serverInfo.server_port,
							log_type: 'I'
						};
	     		    } else {
	     		    	alert("상태 변경 중 오류가 발생했습니다.");
	     		    	changeServerMode(serverId, modeType);
						logData = {
							server_id : serverId,
							message: 'ATE['+serverInfo.server_name+'] '+modeType+'로 변경 중 오류 발생했습니다',
							info : serverInfo.server_ip+":"+serverInfo.server_port,
							log_type: 'E',
							code : "E504"
						};
					}
					saveNmsLog(logData);
				}, function(err){
					console.error(err);
					alert("상태 변경 중 오류가 발생했습니다.");
					//changeServerMode(element,modeType);
					var logData = {
						server_id : serverId,
						message: 'ATE['+serverInfo.server_name+']로 '+modeType+'로 변경 중 오류가 발생했습니다.',
						info : serverInfo.server_ip+":"+serverInfo.server_port+"와의 연결 문제",
						log_type: 'E',
						code : "E701"
					};
					saveNmsLog(logData);
				}, paramData);
				
			}
		}
		
		function getServerInfo(element,type) {			
			var serverId = $(element).attr("data-value");
			var infoWindowTitle = "";
			$.ajax({
     		   url: "/"+type+"/info.do",
     		   dataType: "json",
     		   data : {id : serverId},
     		   type: 'GET',
     		   success: function(data) {
     		     if(data) {
     		    	 if(type == "group") {     	
     		    		infoWindowTitle = "서버 그룹 정보"	    		
     		    		$("#regTypeList").jqxDropDownList("val","GRP");     		    		
     		    		$("#svrGrpName").val(data.group_name);
     		    	 }else{
     		    		infoWindowTitle = "ATE 서버  정보"	
         		    		//console.log("data.server_port:"+data.server_port) ;
     		    		$("#regTypeList").jqxDropDownList("val","SVR");
     		    		$("#svrGrpList").jqxDropDownList("val",data.grp_id); 
     		    		$("#svrGrpList").jqxDropDownList({disabled:true}); 
     		    		$("#svrName").val(data.server_name);
     		    		//$("#macAddr").val(data.server_mac);
     		    		$("#ipAddr").val(data.server_ip);
     		    		$("#svrPort").val(data.server_port);
     		    		//$("#useYn").jqxDropDownList("val",data.status); 
     		    	 }
     		    	$("#regTypeList").jqxDropDownList("disabled",true); 
     		    	
     		    	
     		    	$("#actionType").val("mod");
     		    	$("#sid").val(serverId);
     		    	$("#regServerTitle").html(infoWindowTitle);
     		    	$("#regServerWindow").jqxWindow('open');
     		    	
     		     } else {
     		    	alert("조회된 정보가 없습니다.");
     		     }
     		   },
     		   error : function(err){
     			   console.log(err);
     		   }
     		});
		}
		
		function getServerDataByMac(mac) {
			var result = null;
			$.each(ATE_SVR_LIST,function(idx,data){
				if(mac == data.server_mac){					
					result = data;
					return false;
				}
			});
			return result;
		}
		
		function getServerDataById(id) {
			//console.log("ATE_SVR_LIST :",ATE_SVR_LIST);
			var result = null;
			for(var i=0;i<ATE_SVR_LIST.length;i++){				
				var data = ATE_SVR_LIST[i];				
				if(id == data.id){					
					result = data;
					break;
				}
			}
			
			return result;
		}

		function getServerListByGroup(id) {
			var result = [];
			result = $.map(ATE_SVR_LIST,function(data){
				if(id == data.grp_id){					
					return data;
				}
			});
			
			return result;
		}
		
		function saveNmsLog(paramData){
			$.ajax({
	       		   url: "/nmslog/insert.do",
	       		   dataType: "json",
	       		   data : paramData,
	       		   type: 'PUT',
	       		   success: function(response) {	       		     
	       		   },
	       		   error : function(err){
	       		   }
	       		});
		}
		function openServerLogPopup(serverId){
			
			curServerInfo = getServerDataById(serverId);
			$("#nmsLogWindow").jqxWindow('setTitle',curServerInfo.server_name+" 로그");
			$("#nmsLogWindow").jqxWindow('open');
			$("#popupType").val("ATE");
			searchNmslog();
			
			setTimeout(function(){
				$('#nmsLogGrid').jqxGrid('showcolumn', 'message');
				$('#nmsLogGrid').jqxGrid('hidecolumn', 'server_name');
			},300);	
			
		}
		function doLogout(){
			document.location.href = "/logout.do";
		}

		function changeBindStatus(groupId,status){
			if(status) {
				$("#bindStatusImg_"+groupId).removeClass("off").addClass("on");
				$("#bindStatusInfo_"+groupId).html("MAIN/BACKUP 바인딩");
			} else {
				$("#bindStatusImg_"+groupId).removeClass("on").addClass("off");
				$("#bindStatusInfo_"+groupId).html("바인딩 해제");
			}
		}

		function initErrorAlarm() {
			ion.sound({
			    sounds: [
			        {
			            name: "error_alarm",
			            volume: 1,
			            preload: true,
			            loop: true
			        }
			    ],
			    volume: 1,
			    path: "js/lib/audio/",
			    preload: true
			});
		}
		function setAlarmSoundStatus(){
			var alarmSoundStatus = localStorage.getItem("alarmSoundStatus");
			if(alarmSoundStatus == "on") {
				$(".alarmSound").removeClass("off").addClass("on");
			} else if(alarmSoundStatus == "off") {
				$(".alarmSound").removeClass("on").addClass("off");
			} else {
				localStorage.setItem("alarmSoundStatus","off");
				$(".alarmSound").removeClass("on").addClass("off");
			}			
		}

		function alarmSound(serverId){	
				//var serverInfo = getServerDataById(serverId);

				if(!checkServerAlarm(serverId)){
					//$("#releaseAlarmBox_"+serverId).hide();
				}
				
				controlAlarmSound();	

		}

		function controlAlarmSound(){
			var alarmSoundStatus = localStorage.getItem("alarmSoundStatus");
			if(alarmSoundStatus == "on") {
				if(checkAlarmStatus()) {
					if(!isAlarmRun) {
						isAlarmRun = true;
						setTimeout(function(){
							ion.sound.play("error_alarm");							
						},200);
						
					}
				} else {	
					isAlarmRun = false;					
					setTimeout(function(){
						stopAlarmAudio();						
					},200);
				}
			} else {
				isAlarmRun = false;	
				setTimeout(function(){
					stopAlarmAudio();					
				},200);		
			}
		}

		function stopAlarmAudio(){
			try {
				ion.sound.stop("error_alarm");		
							
			} catch(e) {
				console.log("경고음 stop 상태")
			}
		}

		function displayNICStatus(targetId) {

			var ifKindList= [["nicInput","ifInputLamp"],["nicOutput","ifOutputLamp"],["nicManage","ifManageLamp"]];
			var ifNamesList = nicStatusList["nicDatas_"+targetId].nicNames;			
			var ifStatus = nicStatusList["nicDatas_"+targetId].nicStatus;
			var nicDatas,elementStr = "", statusClass = "";
			for(var j = 0; j<ifKindList.length;j++) {
				nicDatas = nicListByBonding[ifKindList[j][0]];
				elementStr = "";
				statusImg = "";
				for(var i=0;i<nicDatas.length;i++) {
					if(ifNamesList[nicDatas[i]]!== undefined) {
						statusClass = (ifStatus[nicDatas[i]] == 1)?"up":statusClass = "down";
						elementStr+="<div style='padding:5px;height:10px;width:100%;'><div style='float:left;margin-right:5px;'>"+(i+1)+". "+ifNamesList[nicDatas[i]]+"</div><div class='ifLamp "+statusClass+"'></div></div><div style='clear:both'></div>";
						
					}
				}
				
				$("#"+ifKindList[j][1]+"_"+targetId).html(elementStr);
			}
			
		}

		function initSocketEventBind() {
			socket.on('ateLog', function(data){     
				var $logGrid  = $("#logContainer");		
				
				var jsonData = JSON.parse(data);
				if(jsonData.action == "DEL") {
					/*로그 삭제*/
					$.each(jsonData.data,function(idx,info){						
						$logGrid.jqxGrid('deleterow', info.id);
						delAlarmMuteStatus(jsonData.server_id, info.id);
					});
					
				} else {	
					var serverInfo = getServerDataById(jsonData.server_id);
	
					var serverName = serverInfo.server_name;
					var logData= {'id':jsonData.id,'server_id':jsonData.server_id, 'code' : jsonData.code, 'create_date' : jsonData.create_date, 'log_type':jsonData.log_type,'info':jsonData.info,'message':jsonData.message,/*'sid':jsonData.sid,'channelName':jsonData.channelName,*/'serverName': serverName};
					$logGrid.jqxGrid("addrow", null,logData , "first");

					if(!getAlarmInfo(jsonData.server_id,jsonData.id)) {
						setAlarmMuteStatus(jsonData.server_id, jsonData.id,false);
					}

				} 
           }); 

        	socket.on('snmpInfo', function(data){     
        		var jsonData = JSON.parse(data);
        		
        		//console.log("snmpInfo :",jsonData)
        		if(jsonData.nicList) {
	    			var memPieColor = "#3DAF2C";
	    			var cpuPieColor = "#3DAF2C";
	    			var diskPieColor = "#3DAF2C";
	    			var nicInPieColor = "#3DAF2C";
	    			var nicOutPieColor = "#3DAF2C";
	    			var nicMngPieColor = "#3DAF2C";
	
	    			var nicNames = JSON.parse(jsonData.nicList);
	    			var nicStatus = JSON.parse(jsonData.nicStatus);
	    			var tmpNicData = {
	    					"nicNames": nicNames,
	    					"nicStatus" : nicStatus
	    	    	 }
	    			nicStatusList["nicDatas_"+jsonData.server_id] = tmpNicData;
	    			
	//    			jsonData.memUsage
	    			if(jsonData.memUsage > 60 && jsonData.memUsage < 80) memPieColor = "#FFA500";
	    			else if(jsonData.memUsage >= 80) memPieColor = "#C10023";
	    			
	    			if(jsonData.cpuUsage > 60 && jsonData.cpuUsage < 80) cpuPieColor = "#FFA500";
	    			else if(jsonData.cpuUsage >= 80) cpuPieColor = "#C10023";
	    			
	    			if(jsonData.diskUsage > 60 && jsonData.diskUsage < 80) diskPieColor = "#FFA500";
	    			else if(jsonData.diskUsage >= 80) diskPieColor = "#C10023";
	
					var tmpInBps = (jsonData.inputOcts / 4096) * 100;
					var tmpOutBps = (jsonData.outputOcts / 2048) * 100;
					var tmpMngBps = (jsonData.mngOctets / 2048) * 100;
					
	    			if(tmpInBps > 60 && tmpInBps < 80) nicInPieColor = "#FFA500";
	    			else if(tmpInBps >= 80) nicInPieColor = "#C10023";
	
	    			if(tmpOutBps > 60 && tmpOutBps < 80) nicOutPieColor = "#FFA500";
	    			else if(tmpOutBps >= 80) nicOutPieColor = "#C10023";
	
	    			if(tmpMngBps > 60 && tmpMngBps < 80) nicMngPieColor = "#FFA500";
	    			else if(tmpMngBps >= 80) nicMngPieColor = "#C10023";
	
	    			$('#memUsage_'+ jsonData.server_id + ' path').attr("stroke",memPieColor);
		    		$('#memUsage_'+ jsonData.server_id).asPieProgress('go',jsonData.memUsage+'%');
		    		$('#cpuUsage_'+ jsonData.server_id + ' path').attr("stroke",cpuPieColor);		
		    		$('#cpuUsage_'+ jsonData.server_id).asPieProgress('go',jsonData.cpuUsage+'%');
		    		$('#diskUsage_'+ jsonData.server_id + ' path').attr("stroke",diskPieColor);
		    		$('#diskUsage_'+ jsonData.server_id).asPieProgress('go',jsonData.diskUsage+'%');
		    		$('#nicInput_'+ jsonData.server_id + ' path').attr("stroke",nicInPieColor);
		    		$('#nicInput_'+ jsonData.server_id).asPieProgress('go',jsonData.inputOcts);
		    		$('#nicOutput_'+ jsonData.server_id + ' path').attr("stroke",nicOutPieColor);
		    		$('#nicOutput_'+ jsonData.server_id).asPieProgress('go',jsonData.outputOcts);

						displayNICStatus(jsonData.server_id);
				}	
				var serverInfo = getServerDataById(jsonData.server_id);
					setAlarmNotice(serverInfo,jsonData,"SNMP",(jsonData.status == "0")?false:true);
	    		preSnmpStatus["snmp_status_"+jsonData.server_id] = jsonData.status;
				
	    		data = null;
	    		jsonData = null;
	    		serverInfo = null;
	    		tmpNicData = {};
	    		nicNames = null;
	    		nicStatus = null;
    		
            }); 

        	socket.on('conStatus',function(data){
        		var jsonData = JSON.parse(data);
        		var serverInfo = getServerDataById(jsonData.server_id);

       			if(jsonData.action == "DEL") {
       				$("#logContainer").jqxGrid('deleterow', jsonData.id);
       				//console.log("snmp_status :"+jsonData.server_id+","+serverInfo.snmp_status)
       				delAlarmMuteStatus(jsonData.server_id, jsonData.id);
       				//setAlarmNotice(serverInfo,jsonData,"SNMP",(jsonData.snmp_status == "1")?true:false);
           		} else {

           			var logData= {'id':jsonData.id,'server_id':jsonData.server_id, 'code' : jsonData.code, 'create_date' : jsonData.create_date, 'log_type':jsonData.log_type,'info':jsonData.info,'message':jsonData.message,/*'sid':jsonData.sid,'channelName':jsonData.channelName,*/'serverName': serverInfo.server_name};
           			$("#logContainer").jqxGrid("addrow", null,logData , "first");

           			if(!getAlarmInfo(jsonData.server_id,jsonData.id)) {
               			//console.log("conStatus :"+jsonData.server_id+","+jsonData.id)
						setAlarmMuteStatus(jsonData.server_id,jsonData.id,false)
					}
               	}
       			
        		
            });
    		socket.on('sysInfo', function(data) {   		
    			var jsonData = JSON.parse(data);
 				var serverInfo = getServerDataById(jsonData.server_id);
	    		$('#licenceCnt_' + jsonData.server_id).empty().append(jsonData.limit);
	    		$('#allAteCnt_' + jsonData.server_id).empty().append(jsonData.totalChannel);
	    		$('#runAteCnt_' + jsonData.server_id).empty().append(jsonData.running);
	    		$('#stopAteCnt_' + jsonData.server_id).empty().append(jsonData.stop);
	    		$('#errAteCnt_' + jsonData.server_id).empty().append(jsonData.ateError);
	    		$('#errFtpCnt_' + jsonData.server_id).empty().append(jsonData.ftpError);
	    		
				if(ATE_SVR_LIST && ATE_SVR_LIST.length > 0){
					var detectError = (jsonData.ftpError > 0 || jsonData.ateError  > 0);
					setAlarmNotice(serverInfo,jsonData,"SYS",detectError);
				}
	    		
				data = null;
				jsonData = null;
				serverInfo = null;
    		});

		}

		function setAlarmNotice(serverInfo,jsonData,sysType,errorStatus) {
			var changeFlagSysCount = 0;
			var changeFlagSnmpCount = 0;
			
			var statusFlag = 0;
    		//if(jsonData.ftpError > 0 || jsonData.ateError  > 0) {
    		if(errorStatus) {		    			
       			statusFlag = 1;       			
   	    	} else {
   	    		statusFlag = 0
   	    		
   			}
   			//if(sysType == "SNMP")  			console.log("setAlarmNotice :"+sysType+"-"+errorStatus+","+$("#releaseAlarmBox_"+serverInfo.id).hasClass("mute"))	
		  	    		
    		if(sysType == "SYS") {
    			var serverMode = (serverInfo.mode == 0)?false:true;
    			if(serverMode != jsonData.mode) {
    				changeFlagSysCount++;
    			}
    			serverInfo.mode = (jsonData.mode)?1:0;    			
	    		if(serverInfo.status != statusFlag) {
	    			changeFlagSysCount++;
				}
	    		serverInfo.status = statusFlag;
    		} else {    			
    			if(serverInfo.snmp_status != statusFlag) {
    				changeFlagSnmpCount++;	    			
				} 
	    		serverInfo.snmp_status = statusFlag;
            }
            
    		if(changeFlagSysCount > 0 || changeFlagSnmpCount > 0) {        		
                if(serverInfo.status == 0 && serverInfo.snmp_status == 0 ) {
	       			alarmSound(jsonData.server_id);
	            } else {
	            	
	            }
                
    			setTimeout(function(){
        			changeServerStatus(jsonData.server_id);
    			},200);
			}
    			alarmSound(jsonData.server_id);
		}
		
		function getlocatStorageData(){
			var hasStorage = (typeof(Storage) !== "undefined") ? true : false; 			
			if(localStorage.getItem("ateLog")) logFromStorage = JSON.parse(localStorage.getItem("ateLog"));
			else logFromStorage = [];		
		}
		
		function getActiveAteLogData(availSvrList){
			var logSort = function(dataList) {
				dataList.sort(function(a,b){
			    	return a.create_date - b.create_date;
			    });
			};
			logFromStorage = [];
				for(var i = 0;i < availSvrList.length;i++) {
					var serverInfo = availSvrList[i];
					if(serverInfo.server_ip != ""){				
					ems.sync.getActiveLog({
		        		ip: serverInfo.server_ip,
		        		port: serverInfo.server_port
		        	}, function(result){
		        		if(result.result == "success"){			        		
			        		//console.log("getActiveLog :",result)
			        		logFromStorage = $.merge(logFromStorage,result.data);
		        			//logFromStorage = logFromStorage.concat(result.data);
			        		
		        		}else{
		        			saveNmsLog({
		        				server_id: serverInfo.id,
		        				message: 'ATE[' + serverInfo.server_name + ']의  ACTIVE 로그 정보 조회 중 오류가 발생했습니다.',
		        				info: serverInfo.server_ip+":"+serverInfo.server_port,
		        				log_type: 'E',
		        				code : "E505"
		        			})
		        		}
		        	}, function(xhr, status, err){
		        		logFromStorage = $.merge(logFromStorage,[]);
		        		saveNmsLog({
							server_id : serverInfo.id,
							message: 'ATE['+serverInfo.server_name+'] ACTIVE 로그 정보 조회 중 오류가 발생했습니다.',
							info: serverInfo.server_ip+":"+serverInfo.server_port+"와의 연결 문제",
							log_type: 'E',
		        			code : "E701"
						});
		        	});
				}
				

			}

			nms.sync.getNmsActiveLogList(function(result){
				if(result.result){
					for(var i=0;i<result.data.length;i++) {
						result.data[i].id = result.data[i].ref_id;
					}
					logFromStorage = $.merge(logFromStorage,result.data);
					//console.log(logFromStorage)
					//logFromStorage = logSort(logFromStorage);
					
					
				}else{
					saveNmsLog({
        				server_id: serverInfo.id,
        				message: 'NMS ACTIVE 로그 정보 조회 중 오류가 발생했습니다.',
        				info: serverInfo.server_ip+":"+serverInfo.server_port,
        				log_type: 'E',
        				code : "E506"
        			})
				}
			}, function(error){				
				
				saveNmsLog({
    				server_id: serverInfo.id,
    				message: 'NMS ACTIVE 로그 정보 조회 중 오류가 발생했습니다.',
    				info: serverInfo.server_ip+":"+serverInfo.server_port+"와의 연결 문제",
    				log_type: 'E',
    				code : "E701"
    			})
			});

			logFromStorage = logFromStorage.sort(function(a,b){
		    	return a.create_date - b.create_date;
		    });
			//console.log(logFromStorage)
    		var theSrc = $("#logContainer").jqxGrid("source");
    		theSrc._source.localdata = logFromStorage;
    		$("#logContainer").jqxGrid("updatebounddata");
    		
			for(var i = 0; i<logFromStorage.length ;i++){
				setAlarmMuteStatus(logFromStorage[i].server_id,logFromStorage[i].id,false);
				var serverId = logFromStorage[i].server_id;
				//if($("#releaseAlarmBox_"+serverId).is(":hidden")){
				//	$("#releaseAlarmBox_"+serverId).show();
				//}
				
			}
			
		}
		
		function setlocatStorageData(){
			var hasStorage = (typeof(Storage) !== "undefined") ? true : false; 			
			if(hasStorage) localStorage.setItem("ateLog",JSON.stringify(logFromStorage));
		}

		function restartSnmpService(){
			//if(confirm("서버를 재시작 하시겠습니까?")){
				$.post("/admin/restartSnmp",function(data){
					console.log("SNMP 재시작 : ",data.result);
	    		},"json");
			//}
		}
		
		function checkAlarmStatus(){
			var result = false;
			for(key1 in alarmMuteStatus) {
				if(Object.keys(alarmMuteStatus).length > 0) {
				   var svrAlarmData = alarmMuteStatus[key1];
				   //console.log("svrAlarmData :",svrAlarmData)
				   if(Object.keys(svrAlarmData).length > 0) {
				   		for(key2 in svrAlarmData) {					   
						   var muteValue = svrAlarmData[key2];
						   
						   if(!muteValue) {
							   result = true;
							   break;
						   } 
						}
				   }
				}
				if(result) {
					break;
				}
			}

			return result;
		}
		function delAlarmMuteStatus(serverId,evtId) {
			delete alarmMuteStatus["server_"+serverId][evtId];
		}
		
		function setAlarmMuteStatus(serverId, evtId, status) {
			//status true is ADD false is DELETE 
			
			if(!alarmMuteStatus["server_"+serverId]) {
				alarmMuteStatus["server_"+serverId] = {};
			}
			
			if(evtId) {
				alarmMuteStatus["server_"+serverId][evtId] = status;
			} else {
				var svrAlarmData = alarmMuteStatus["server_"+serverId];
				//console.log("===================== :",Object.keys(svrAlarmData).length);
			   	if(Object.keys(svrAlarmData).length > 0) {				   
			   		for(key in svrAlarmData) {					   
					  svrAlarmData[key] = true;			   
					}
			   }
			}
		}
		
		function checkServerAlarm(serverId) {
			var result = false;
			var serverAlarmData = alarmMuteStatus["server_"+serverId];
			for(key in serverAlarmData) {
				if(Object.keys(serverAlarmData).length > 0) {
				   var svrMute = serverAlarmData[key];
				   if(!svrMute) {
						result = true;
						break;
				   }	
				} else {
					break;
				}
				
			}
			return result;
		}
		
		function getAlarmInfo(serverId,evtId) {
			if(!alarmMuteStatus["server_"+serverId]) {
				alarmMuteStatus["server_"+serverId] = {};
			}
			return alarmMuteStatus["server_"+serverId][evtId];
		}
    </script>
</head>
<body>
<div style="width:100%;height:100%;overflow:hidden;">
	<div id="mainSplitter">
          <div id="mainContainer" style="overflow: auto;">
          	<div style="width:100%;height:50px;">
          		<h1 style="float:left;width:50%;padding-left:20px;">AMUZLAB NMS</h1>
          		<div class="alarmSound" style="float:right;margin:5px;"></div>
          		<div style="float:right;margin:5px;"><input type="button" id="btnLogout" value="로그아웃"/></div>
          	</div>
          	<div style="clear:both;">
          		<sec:authorize access="hasRole('ROLE_ADMIN') and isAuthenticated()">
				<a class="addServer" id="btnOpenRegPopup"></a>
				<a class="userMgr" id="btnOpenUserPopup"></a>
				</sec:authorize>
				
				<a class="logServer" id="btnOpenLogPopup"></a>
				
			</div>
			<div id="contentContainer">          		
				
			</div>
		</div>
		<div>
			<div style="background:#484848;height:100%;margin:5px;overflow: hidden;">
				<div id="logContainer"></div>
			</div>
		</div>
	</div>
	<div id="regServerWindow">
	    <div id="regServerTitle">그룹및 서버 등록</div>
	    <div>
    		<form id="frmRegServer" >
    			<input type="hidden" name="actionType" id="actionType"/>
    			<input type="hidden" name="serverType" id="serverType"/>
    			<input type="hidden" name="sid" id="sid"/>
    			<input type="hidden" name="popupType" id="popupType"/>
    			<div class="configLabel" style="padding-top: 10px;"><label>등록대상: </label><div style="float:left;"><div id="regTypeList"></div></div></div><br>		
			    
			    <div id="svrGrpRegForm" style="display:none;">
			    	<div class="configLabel"><label>서버그룹명: </label></div><div style="float:left;"><input type="text" id="svrGrpName" name="svrGrpName"/></div>			    	
			    </div>
			    <div id="svrRegForm" style="display:none;">
			    	<div class="configLabel"><label>서버그룹: </label></div><div style="float:left;"><div id="svrGrpList"></div></div>
			    	<div class="configLabel"><label>서버명: </label></div><div style="float:left;"><input type="text" id="svrName" name="svrName"/></div><br>		
			    	<!-- div class="configLabel"><label>MAC 주소: </label></div><div style="float:left;"><input type="text" id="macAddr" name="macAddr"/></div--><br>
			    	<div class="configLabel"><label>IP 주소: </label></div><div style="float:left;"><input type="text" id="ipAddr" name="ipAddr"/></div><br>
			    	<div class="configLabel"><label>PORT: </label></div><div style="float:left;"><input type="text" id="svrPort" name="svrPort"/></div><br>
			    	<!-- div class="configLabel"><label>사용: </label></div><div style="float:left;"><div id="useYn"></div></div-->		    	   	
			    </div>
			    <div style="clear:both;padding:30px 10px 10px 10px;text-align:center;"><input type="button" value="저장" style="margin-bottom: 5px;" id="btnRegServer" /><input type="button" value="닫기" id="btnCancelReg" /></div>
			</form>
	    </div>
	</div>
	<div id="nmsLogWindow">
	    <div>NMS 로그</div>
	    <div>
    		<form id="frmLogServer" >
			    <div id="logSearchForm">
			    	<div style="float:left;"><div id="searchDate"></div></div><div style="float:left;"><div id="searchLogType"></div></div><div style="float:left;"><input type="button" id="btnSearchLog" value="검색"/></div>		    	
			    </div>
			    <div id="nmsLogGrid"></div>
			 </form>
	    </div>
	</div>
	<div id="userMgrWindow">
	    <div>사용자 관리</div>
	    <div>
	    	<div id="userMgrContainer"></div>
	    	<div style="clear:both;padding:30px 10px 10px 10px;text-align:center;"><input type="button" value="닫기" id="btnCancelNic" /></div>		   
	    </div>
	</div>
	<div id="nicStatusWindow">
	    <div>NIC 상태</div>
	    <div id="nicStatusContainer">		   
	    </div>
	</div>
	
</div>
<div id="dataLoader"></div>
</body>
<div id="socketLoader"></div>
</body>
</html>