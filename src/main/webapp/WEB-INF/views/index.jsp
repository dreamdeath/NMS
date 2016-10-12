<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta name="description" content="THUMBNAIL EXTRACTOR" />
    <title id='Description'>AMUZLAB EXTRACTOR NMS</title>
    <link href="../../css/jqx.base.css" rel="stylesheet" type="text/css" />
    <link href="../../css/jqx.black.css" rel="stylesheet" type="text/css" />
    <link href="../../css/main.css" rel="stylesheet" type="text/css" />
    <link href="../../css/progress.css" rel="stylesheet" type="text/css" />
    <script src="../../js/rainbow.min.js"></script>
	<script type="text/javascript" src="../../js/lib/jquery-1.11.1.min.js"></script>
	<script type="text/javascript" src="../../js/lib/socket.io-1.4.5.js"></script>	
	<script src="../../js/lib/jqx-all.js"></script>
	<script src="../../js/utils.js"></script>				
	<script src="../../js/jquery-asPieProgress.min.js"></script>	
	
    <script type="text/javascript">
		$.jqx.theme = 'black';
		var isFirstLoaded = true;
		// var socket = io.connect(location.origin); 
		var socket = io.connect("http://localhost:10000"); 
		var hasStorage = (typeof(Storage) !== "undefined") ? true : false; 
		var logFromStorage;
		if(localStorage.getItem("ateLog")) logFromStorage = JSON.parse(localStorage.getItem("ateLog"));
		else logFromStorage = [];			

		var ATE_SVR_LIST = [];
		var nmsLogSource;
        $(document).ready(function () {   
        	
        	 $('#mainSplitter').jqxSplitter({ width: "100%", height: "100%", orientation: 'horizontal', panels: [{ size: '70%' }, { size: "30%" ,min:100}] });
        
        	/*로그 그리드 생성*/
        	createLogGrid();
        	
        	socket.on('ateLog', function(data){     
				var $logGrid  = $("#logContainer");		
				var jsonData = JSON.parse(data);
				console.log(jsonData)
				//$logDiv.prepend("<div>"+data.info+"</div>");
				var message = jsonData.info;
			
				if(jsonData.code.search("E") == 0){
					message = jsonData.message;
				}
				var serverInfo = getServerDataByMac(jsonData.mac);
				console.log(serverInfo);
				var serverName = serverInfo.server_name;
				$logGrid.jqxGrid("addrow", null, {'id':jsonData.id, 'code' : jsonData.code, 'createDate' : jsonData.createDate, 'type':jsonData.type,'message':jsonData.message,'info':message,'sid':jsonData.sid,'channelName':jsonData.channelName,serverName: serverName}, "first");
				var totalRowSize = $logGrid.jqxGrid('getrows').length;
				console.log("totalRowSize :",totalRowSize);
				if(totalRowSize > 100) {
					var id = $logGrid.jqxGrid('getrowid', totalRowSize - 1);
					console.log(id)
                    var commit = $logGrid.jqxGrid('deleterow', id);
				}
				console.log(logFromStorage)
				if(hasStorage) {
					logFromStorage.push(jsonData);
					if(logFromStorage.length > 30) {
						logFromStorage.shift();
					}
					localStorage.setItem("ateLog",JSON.stringify(logFromStorage));
					
				}
            }); 
        	
        	/*
        	socket.on('connect', function() {
    			output('<span class="connect-msg">Client has connected to the server!</span>');
    		});
        	socket.on('disconnect', function() {
    			output('<span class="disconnect-msg">The client has disconnected!</span>');
    		});
        	*/
    		socket.on('sysInfo', function(data) {   			
    			
    			var jsonData = JSON.parse(data);
    			var macAddr = jsonData.mac;
    			var info = getServerDataByMac(macAddr);
    			if(info) {  
    				var memPieColor = "#3DAF2C";
    				var cpuPieColor = "#3DAF2C";
    				var diskPieColor = "#3DAF2C";
    				jsonData.memUsage
    				if(jsonData.memUsage > 60 && jsonData.memUsage < 80) memPieColor = "#FFA500";
    				else if(jsonData.memUsage >= 80) memPieColor = "#C10023";
    				
    				if(jsonData.cpuUsage > 60 && jsonData.cpuUsage < 80) cpuPieColor = "#FFA500";
    				else if(jsonData.cpuUsage >= 80) cpuPieColor = "#C10023";
    				
    				if(jsonData.diskUsage > 60 && jsonData.diskUsage < 80) diskPieColor = "#FFA500";
    				else if(jsonData.diskUsage >= 80) diskPieColor = "#C10023";
    				
    				$('#memUsage_'+info.id+' path').attr("stroke",memPieColor);
	    			$('#memUsage_'+info.id).asPieProgress('go',jsonData.memUsage+'%');
	    			$('#cpuUsage_'+info.id+' path').attr("stroke",cpuPieColor);
	    			$('#cpuUsage_'+info.id).asPieProgress('go',jsonData.cpuUsage+'%');
	    			$('#diskUsage_'+info.id+' path').attr("stroke",diskPieColor);
	    			$('#diskUsage_'+info.id).asPieProgress('go',jsonData.diskUsage+'%');
	    			
	    			
	    			
    			}
    			//output('<span class="username-msg">' + data + '</span> ');
    		});
    		
    		
    		//var jsonObject = {id: "getdream",
            //        message: "test"};
  			//socket.emit('chatevent', jsonObject);  			
			initRegPopup();			
	    	initRegTypeList();
	    	initUseYnList();
	    	getServerGroupList();
	    	initLogPopup();	

			
			$("#svrGrpName").jqxInput({placeHolder: "서버 그룹명을 입력하세요", height: 25, width: 150, minLength: 1});
			$("#svrName").jqxInput({placeHolder: "서버명을 입력하세요", height: 25, width: 150, minLength: 1});
			$("#macAddr").jqxMaskedInput({ width: 150, height: 25, mask: 'AA:AA:AA:AA:AA:AA',promptChar:" "});
			$("#ipAddr").jqxMaskedInput({ width: 150, height: 25, mask: '###.###.###.###',promptChar:" "});
       	    $("#btnOpenRegPopup").on("click",openRegPopup);
       	 	$("#btnOpenLogPopup").on("click",openNmsLogPopup); 

       	 	$("#searchDate").jqxDateTimeInput({ width: 250, height: 25,  formatString: "yyyy-MM-dd",selectionMode: 'range' });
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

       			 	$('#btnRegServer').jqxButton({disabled: false });
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
				if(confirm(cnfmMessage)) {
					saveServerInfo(actionType);
				}
			});
        });      
        
        function saveServerInfo(actionType){
        	var actionURL = "";
        	var paramData = {};
        	var item = $("#regTypeList").jqxDropDownList('getSelectedItem'); 
        	var logInfo = "";
        	var targetId = "0";
        	if(actionType == "add") {      		
				var cnfmMessage = "";
				if(item.value == "GRP") {
					actionURL = "/group/insert.do";
					paramData = {
	        				group_name : $("#svrGrpName").val()
	        		};
					
					logInfo = "그룹["+$("#svrGrpName").val()+"]";
				} else {
					actionURL = "/server/insert.do";
					paramData = {							
	        				server_name : $("#svrName").val(),
	        				server_ip : $("#ipAddr").val(),
	        				server_mac : $("#macAddr").val(),
	        				status : $("#useYn").val(),
	        				grp_id : $("#svrGrpList").val()
	        		}
					
					logInfo = "서버["+$("#svrName").val()+"]";
				}
        		
        	} else {
        		if(item.value == "GRP") {
					actionURL = "/group/update.do";
					paramData = {
							id : $("#sid").val(),
	        				group_name : $("#svrGrpName").val()
	        		};
					logInfo = "그룹["+$("#svrGrpName").val()+"]";
				} else {
					actionURL = "/server/update.do";
					paramData = {
							id : $("#sid").val(),
	        				server_name : $("#svrName").val(),
	        				server_ip : $("#ipAddr").val(),
	        				server_mac : $("#macAddr").val(),
	        				status : $("#useYn").val(),
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
       						logInfo+=" 추가";
	       		     } else {
       						logInfo+=" 정보 수정";       		    	
	       		     }
       		    	var logData = {
		 	     		    	server_id : targetId,
		 	     		    	info: logInfo,
		 	     		    	log_type: 'I'
		 	     	 };
    		    	 saveNmsLog(logData);
       		     } else {
       		    	alert("저장중 오류가 발생했습니다.");
       		    	if(actionType == "add") {      		
	   						logInfo+=" 추가중 오류";
	       		     } else {
	   						logInfo+=" 정보 수정중 오류";       		    	
	       		     }
       		    	var logData = {
		 	     		    	server_id : targetId,
		 	     		    	info: logInfo,
		 	     		    	log_type: 'E'
		 	     		};
    		    	 saveNmsLog(logData);
       		     }
       		   },
       		   error : function(err){
					if(actionType == "add") {      		
						logInfo+=" 추가중 오류";
					} else {
						logInfo+=" 정보 수정중 오류";       		    	
					}
	       			var logData = {
		 	     		    	server_id : targetId,
		 	     		    	info: logInfo,
		 	     		    	log_type: 'E'
		 	     		};
			    	 saveNmsLog(logData);
       		   }
       		});
        }
        
        function output(message) {
            var currentTime = "<span class='time'>" + new Date() + "</span>";
            var element = $("<div>" + currentTime + " " + message + "</div>");
			$('#mainContainer').prepend(element);
		}
        function createLogGrid(){
        	var logData = [];
        	console.log(logFromStorage)
        	var errorLogSource =
            {
                datatype: "json",
                datafields: [
                 { name: 'id', type: 'string' },
                 { name: 'code', type: 'string' },
                 { name: 'createDate', type: 'string' },
                 { name: 'channelName', type: 'string' },
                 { name: 'sid', type: 'string' },
                 { name: 'type', type: 'string' },
                 { name: 'confirm', type: 'bool' },
                 { name: 'info', type: 'string' },
                 { name: 'message', type: 'string' },
                 { name: 'serverName', type: 'string' }
                    
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
                  { text: '서버', datafield: 'serverName', width: 150 ,editable: false},
	                 { text: '날짜', datafield: 'createDate', width: 150 ,editable: false},
	                 { text: '채널명', datafield: 'channelName', width: 150 ,editable: false},
	                 { text: 'SID', datafield: 'sid', width: 80 ,editable: false},
	                 { text: '코드', datafield: 'code', width: 100 ,editable: false},
	                 { text: '내용', datafield: 'info', renderer:logrenderer ,editable: false}
	                 
              ]
            });
            /*
            $('#errorLogGrid').on('bindingcomplete', function (event) {
	        	   $("#errorLogGrid").jqxGrid("addrow", null, {'id':'1', 'code' : '200', 'createDate' : '2016-08-18 29:11:11', 'type':'state','message':'테스트 로그1','channelInfo':{},confirm:true}, "first");
	        	   $("#errorLogGrid").jqxGrid("addrow", null, {'id':'2', 'code' : '200', 'createDate' : '2016-08-18 29:11:11', 'type':'state','message':'테스트 로그2','channelInfo':{},confirm:false}, "first");
	        	   $("#errorLogGrid").jqxGrid("addrow", null, {'id':'3', 'code' : '200', 'createDate' : '2016-08-18 29:11:11', 'type':'state','message':'테스트 로그3','channelInfo':{},confirm:true}, "first");
	        	   $("#errorLogGrid").jqxGrid("addrow", null, {'id':'4', 'code' : '200', 'createDate' : '2016-08-18 29:11:11', 'type':'state','message':'테스트 로그4','channelInfo':{},confirm:true}, "first");
	        	   $("#errorLogGrid").jqxGrid("addrow", null, {'id':'5', 'code' : '200', 'createDate' : '2016-08-18 29:11:11', 'type':'state','message':'테스트 로그5','channelInfo':{},confirm:true}, "first");
	        	   $("#errorLogGrid").jqxGrid("addrow", null, {'id':'6', 'code' : '200', 'createDate' : '2016-08-18 29:11:11', 'type':'state','message':'테스트 로그6','channelInfo':{},confirm:true}, "first");
	        	   $("#errorLogGrid").jqxGrid("addrow", null, {'id':'7', 'code' : '200', 'createDate' : '2016-08-18 29:11:11', 'type':'state','message':'테스트 로그7','channelInfo':{},confirm:true}, "first");
	        	   
	        	 });
            */
			var logrenderer = function (value) {
				return '<div style="text-align: center;margin-top: 5px;">' + value + '</div>';
			}
        }
        function initLogGridPopup(){
        	nmsLogSource =
            {
                datatype: "json",
                datafields: [
                 { name: 'id', type: 'string' },
                 { name: 'server_id', type: 'string' },
                 { name: 'server_name', type: 'string' },
                 { name: 'createDate', type: 'string' },
                 { name: 'info', type: 'string' },
                 { name: 'create_date', type: 'string' },
                 { name: 'log_type', type: 'string' }                    
                ],
                id: 'id',
                url: "/nmslog/list.do",
                /*
                data: {
                	search_start : $("#searchDate").jqxDateTimeInput('getRange').from.getFromFormat('yyyy-mm-dd hh:ii:ss'),
                	search_end : $("#searchDate").jqxDateTimeInput('getRange').to.getFromFormat('yyyy-mm-dd hh:ii:ss')
                }
                */
                formatdata:function(data){
                	data.search_start = $("#searchDate").jqxDateTimeInput('getRange').from.getFromFormat('yyyy-mm-dd hh:ii:ss');
                	data.search_end = $("#searchDate").jqxDateTimeInput('getRange').to.getFromFormat('yyyy-mm-dd hh:ii:ss');
                	return data;
                	return {
                     	search_start : $("#searchDate").jqxDateTimeInput('getRange').from.getFromFormat('yyyy-mm-dd hh:ii:ss'),
                     	search_end : $("#searchDate").jqxDateTimeInput('getRange').to.getFromFormat('yyyy-mm-dd hh:ii:ss')
                     }
                },
                beforeprocessing: function(data) {
                    if (data != null && data.length > 0) {
                    	nmsLogSource.totalrecords = 18;//data[0].totalRecords;
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
                  { text: '종류', datafield: 'log_type', width: 100},
                  { text: '내용', datafield: 'info'},
	              { text: '날짜', datafield: 'create_date', width: 150}
	                 
	                 
              ]
            });
            
            var getSearchCondition = function(){
				console.log($("#searchDate").jqxDateTimeInput('getRange').from)
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
                cancelButton: $('#btnCancelReg'),
                initContent: function () {
                    $('#btnRegServer').jqxButton({ width: '80px', disabled: true });
                    $('#btnCancelReg').jqxButton({ width: '80px', disabled: false });

                }
            });
        	
        	$('#regServerWindow').on('close', function (event) {                
                $("form")[0].reset();
                $("#svrGrpList").jqxDropDownList('selectIndex', 0 ); 
                $("#useYn").jqxDropDownList('selectIndex', 0 ); 
                $("#regTypeList").jqxDropDownList("disabled",false); 
 		    	$("#svrGrpList").jqxDropDownList("disabled",false); 
   
            });
        }
        
        function initLogPopup(){        	
        	$('#nmsLogWindow').jqxWindow({  width: 700,
                height: 500, resizable: true,                
                autoOpen:false
            });
        	$("#btnSearchLog").jqxButton({ width: '80px'});
        	$("#btnSearchLog").on("click",searchNmslog);
        }
        function searchNmslog(){
        	$("#nmsLogGrid").jqxGrid('updatebounddata');
        }
        function openRegPopup(){
        	$("#actionType").val("add");
        	$("#regServerWindow").jqxWindow('open');
        }
        function openNmsLogPopup(){
        	$("#nmsLogWindow").jqxWindow('open');
        }
        
        function getServerGroupList(){
        	$.get("/group/list.do",function(data){
        		initContantsLayout(data);
        		initServerGroupList(data);
        	},"json");
        }
        
        function initServerGroupList(data) {

        	var dataAdapter = new $.jqx.dataAdapter(data);
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
        function initContantsLayout(data){
        	var grpElement = "";
        	var svrElement = "";
        	var grpId = "";
        	var grpName = "";
        	var serverStatus = "";
        	var buttonStatus = "";
        	for(var i = 0;i < data.length;i++){
        		grpId = data[i].id;
        		grpName = data[i].group_name;
        		grpElement = '<fieldset style="float:left;position: relative;" class="srvGrpContainer" id="srvGrpWrap_'+grpId+'">';
        		grpElement+= '<legend class="svrGrpLegend" data-value="'+grpId+'">'+data[i].group_name+'</legend>';
        		grpElement+= '</fieldset>';    			
    			$("#contentContainer").append(grpElement);

        		$.get("/server/list.do",
        			{
        				id : grpId,
        				group_name : grpName
        			},function(svrData){
        				if(svrData.list.length > 0) {
		        			$.each(svrData.list,function(idx,dt){
		        				//console.log("grpData.id :",grpId);	
		        				ATE_SVR_LIST.push(dt);
		        				if(dt.status == 1) {
		        					serverStatus = "run";
		        					buttonStatus = "stop";
		        				} else {
		        					serverStatus = "stop";
		        					buttonStatus = "run";
		        				}
		        				svrElement = "";
		        				svrElement+='<fieldset style="float:left;width:70%" class="svrInfoWrap">';
		        				svrElement+='	<legend>'+dt.server_name+'</legend>';
		        				svrElement+='	<div style="float:left;" id = "serverImg_'+dt.id+'" class="serverImgBox '+serverStatus+'"></div>';
		        				svrElement+='	<div style="float:left;padding-left:20px;">';
		        				svrElement+='	   <div>전체 ATE : <span id="allAteCnt_'+dt.id+'" class="serverInfo-text"></span></div>';
		        				svrElement+='	   <div>실행중 ATE : <span id="runAteCnt_'+dt.id+'" class="serverInfo-text"></span></div>';
		        				svrElement+='	   <div>중지 ATE : <span id="stopAteCnt_'+dt.id+'" class="serverInfo-text"></span></div>';
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
		        				svrElement+='		<div>';
		        				//svrElement+='	   <div class="tiny-chartbox"><div id="memUsage_'+dt.id+'" chart-type="donut" data-chart-max="100" data-chart-segments="{ \'0\':[\'0\',\'30\',\'#19A7F5\'],  \'1\':[\'30\',\'70\',\'#ecebeb\'] }" data-chart-text="30%" data-chart-caption="MEM"  data-chart-initial-rotate="180"></div></div>';
		        				svrElement+='	</div>';
		        				svrElement+='</fieldset>';
		        				svrElement+='<div style="float:left;width: 120px;height: 120px;padding: 15px 5px 5px 15px;">';
		        				svrElement+='	<div style="float:left" class="operation info" data-value="'+dt.id+'"></div>';
		        				svrElement+='	<div style="clear:both;"></div>';	
		        				svrElement+='	<div style="float:left" class="operation del" data-value="'+dt.id+'"></div>';
		        				svrElement+='	<div style="float:left" id = "serverOperation_'+dt.id+'" class="operation '+buttonStatus+'"  data-value="'+dt.id+'"></div>';
		        					        				
		        				svrElement+='	<div style="float:left"></div>';
		        				svrElement+='</div>';
		        				svrElement+='<div style="clear:both;"></div>';
		        				$("#srvGrpWrap_"+dt.grp_id).append(svrElement);
		        				
		        				$("#serverImg_"+dt.id).on("click",function(){
			        				window.open('http://'+dt.server_ip.replaceAll(' ','')+':4001/nmsManager/nmsadmin','_blank');
			        			});
		        				
		        				createUsageCircle($("#memUsage_"+dt.id));
		        				createUsageCircle($("#cpuUsage_"+dt.id));
		        				createUsageCircle($("#diskUsage_"+dt.id));
		        				
		        			});
		        			
		        			console.log(ATE_SVR_LIST)
		        			$("#srvGrpWrap_"+svrData.grp_id +" .operation").on("click",function(){
		        				if($(this).hasClass("del")) {
		        					delServer(this,"server");
		        				} else if($(this).hasClass("info")) {
		        					getServerInfo(this,"server");
		        				} else  {      					
		        					runServer(this,"server");
		        				}
		                		
		                	});
		        			
		        			
        				} else {
        					$("#srvGrpWrap_"+svrData.grp_id).append("<div>그룹 "+svrData.grp_nm+"에 등록된 서버가 없습니다.</div>");
        				}
        			//$("#contentContainer").append(grpElement);
        		},"json")
        		$("#srvGrpWrap_"+grpId).append("<div style='position:absolute;right:5px;bottom:5px;'><button id='btnDelGroup_"+grpId+"' data-value='"+grpId+"'>그룹삭제</button></div>");
        		$("#btnDelGroup_"+grpId).jqxButton({ width: '100', height: '25'});
        		$("#btnDelGroup_"+grpId).on("click",function(){
            		delServer(this,"group");
            	});
        	};
        	$(".svrGrpLegend").on("click",function(){
        		getServerInfo(this,"group");
        	});
        	
/*
				<fieldset style="float:left;width:400px" class="svrInfoWrap">
					<legend>ATE 1</legend>
					<div style="float:left;" class="serverImgBox run"></div>
					<div style="float:left;">
					   <div>서버상태 : <span id="cpuUsage1" class="serverInfo-text"></span>
					   <span id="cpuUsage1" class="serverInfo-text"></span>
					   <span id="cpuUsage1" class="serverInfo-text"></span></div>
					</div>
				</fieldset>
				<div style="float:left;width: 120px;height: 120px;padding: 15px 5px 5px 15px;">
					<div style="float:left" class="operation info"></div>
					<div style="float:left" class="operation del"></div>
					<div style="clear:both;"></div>
					<div style="float:left" class="operation stop"></div>
					<div style="float:left"></div>
				</div>
				<div style="clear:both;"></div>
				*/
        }
        
        function createUsageCircle(element){
        	element.asPieProgress({
                namespace: 'pie_progress'
            });
        }
        function initLayout(){
        	$("#contentContainer").empty();   	
	    	getServerGroupList();
        }
        
        function delServer(element,type) {
        	var confirmMessage = "";
        	var serverId = $(element).attr("data-value");
        	
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
        		var serverInfo = getServerDataById(serverId);
        		targetName = "서버["+serverInfo.server_name+"]";
        	}
        	
        	if(confirm("선택한 "+confirmMessage+" 삭제하시겠습니까?\n삭제 후에는 복구하실 수 없습니다.")){
        		
        		$.ajax({
            		   url: "/"+type+"/del.do",
            		   dataType: "json",
            		   data : {id : serverId},
            		   type: 'POST',
            		   success: function(response) {
            		     if(response.result) {
            		    	 alert("정상적으로 삭제되었습니다.");
            		    	 initLayout();
            		    	 var logData = {
     		 	     		    	server_id : serverId,
     		 	     		    	info: targetName+" 삭제",
     		 	     		    	log_type: 'I'
     		 	     		};
            		    	 saveNmsLog(logData);
            		     } else {
            		    	 var logData = {
      		 	     		    	server_id : serverId,
      		 	     		    	info: targetName+" 삭제중 오류",
      		 	     		    	log_type: 'ㄸ'
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
		
        function changeServerStatus(element,mode){
        	var serverId = $(element).attr("data-value");
        	$.ajax({
        		   url: "/server/state.do",
        		   dataType: "json",
        		   data : {
        			   id : serverId,
        		   	   status : mode
        		   },
        		   type: 'PUT',
        		   success: function(response) {
        		     if(response.result) {        		    	
      		        	if($(element).hasClass("run")){
      						$("#serverImg_"+serverId).removeClass("stop").addClass("run");
      						$("#serverOperation_"+serverId).removeClass("run").addClass("stop");
      					} else {
      						console.log(serverId)
      						$("#serverImg_"+serverId).removeClass("run").addClass("stop");
      						$("#serverOperation_"+serverId).removeClass("stop").addClass("run");
      					}
        		     }
        		   },
        		   error : function(err){
        			   console.log(err);
        		   }
        		});
        	
        }
		function runServer(element) {
			var modeType = "";
			var returnMessage = "";			
			
			if($(element).hasClass("run")){
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
			} else {
				modeType = "standby";
				returnMessage = "중지";
			}
			var paramData = {
					mode : modeType
			};
			if(confirm("서버를 "+returnMessage+"하시겠습니까?")){		
				var serverId = $(element).attr("data-value");
				var serverInfo = getServerDataById(serverId);
				$.ajax({
	     		   url: "http://"+serverInfo.server_ip+":4001/mode",
	     		   dataType: "json",
	     		   data : JSON.stringify(paramData),
	     		   contentType: "application/json; charset=utf-8",
	     		   type: 'POST',
	     		   success: function(response) {
	     		     if(response.result) {
	     		    	alert("정상적으로 "+returnMessage+"되었습니다.");     		    	
	     		    	changeServerStatus(element, modeType);
	     		    	var logData = {
    	     		    	server_id : serverId,
    	     		    	info: 'ATE['+serverInfo.server_name+']를 '+modeType+'로 변경',
    	     		    	log_type: 'I'
    	     		     }
	     		     } else {
	     		    	alert("상태 변경 중 오류가 발생했습니다.");
	     		    	changeServerStatus(element, modeType);
	     		    	var logData = {
		 	     		    	server_id : serverId,
		 	     		    	info: 'ATE['+serverInfo.server_name+'] '+modeType+'로 변경 중 오류 발생',
		 	     		    	log_type: 'E'
		 	     		     }
	     		     }
	     		     
	     		     saveNmsLog(logData);
	     		   },
	     		   error : function(err){
	     			   console.log(err);
	     			  alert("상태 변경 중 오류가 발생했습니다.");	     			 
	     			  //changeServerStatus(element,modeType);
	     			 var logData = {
		 	     		    	server_id : serverId,
		 	     		    	info: 'ATE['+serverInfo.server_name+']로 '+modeType+'로 변경 중 오류 발생',
		 	     		    	log_type: 'E'
		 	     	};
		     		saveNmsLog(logData);
	     		   }
	     		});
			}
		}
		
		function getServerInfo(element,type) {			
			var serverId = $(element).attr("data-value");
			$.ajax({
     		   url: "/"+type+"/info.do",
     		   dataType: "json",
     		   data : {id : serverId},
     		   type: 'GET',
     		   success: function(data) {
     		     if(data) {
     		    	 if(type == "group") {     		    		
     		    		$("#regTypeList").jqxDropDownList("val","GRP");     		    		
     		    		$("#svrGrpName").val(data.group_name);
     		    	 }else{
     		    		var tmpIp = data.server_ip.split(".");
     		    		var newIpFormat = $.map(tmpIp,function(data){
     		    			return data.lpad(3," ");
     		    		}).join(".");

     		    		$("#regTypeList").jqxDropDownList("val","SVR");
     		    		$("#svrGrpList").jqxDropDownList("val",data.grp_id); 
     		    		$("#svrGrpList").jqxDropDownList({disabled:true}); 
     		    		$("#svrName").val(data.server_name);
     		    		$("#macAddr").val(data.server_mac);
     		    		$("#ipAddr").val(newIpFormat);
     		    		$("#useYn").jqxDropDownList("val",data.status); 
     		    	 }
     		    	$("#regTypeList").jqxDropDownList("disabled",true); 
     		    	
     		    	
     		    	$("#actionType").val("mod");
     		    	$("#sid").val(serverId);
     		    	$("#regServerWindow").jqxWindow('open');
     		    	setTimeout(function(){
     		    		$('#btnRegServer').jqxButton({disabled: false });     		    		
     		    	},500);
     		    	
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
			var result = null;
			$.each(ATE_SVR_LIST,function(idx,data){
				if(id == data.id){
					result = data;
					return false;
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
    </script>
</head>
<body>
<div style="width:100%;height:100%;overflow:hidden;">
	<div id="mainSplitter">
          <div id="mainContainer" style="overflow: auto;">
          	<div style="width:100%;height:50px;">
          		<h1>AMUZLAB NMS</h1>
          	</div>
          	<div>
				<a class="addServer" id="btnOpenRegPopup"></a>
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
	    <div>그룹및 서버 등록</div>
	    <div>
    		<form id="frmRegServer" >
    			<input type="hidden" name="actionType" id="actionType"/>
    			<input type="hidden" name="sid" id="sid"/>
    			<div class="configLabel" style="padding-top: 10px;"><label>등록대상: </label><div style="float:left;"><div id="regTypeList"></div></div></div><br>		
			    
			    <div id="svrGrpRegForm" style="display:none;">
			    	<div class="configLabel"><label>서버그룹명: </label></div><div style="float:left;"><input type="text" id="svrGrpName" name="svrGrpName"/></div>			    	
			    </div>
			    <div id="svrRegForm" style="display:none;">
			    	<div class="configLabel"><label>서버그룹: </label></div><div style="float:left;"><div id="svrGrpList"></div></div>
			    	<div class="configLabel"><label>서버명: </label></div><div style="float:left;"><input type="text" id="svrName" name="svrName"/></div><br>		
			    	<div class="configLabel"><label>MAC 주소: </label></div><div style="float:left;"><input type="text" id="macAddr" name="macAddr"/></div><br>
			    	<div class="configLabel"><label>IP 주소: </label></div><div style="float:left;"><input type="text" id="ipAddr" name="ipAddr"/></div><br>
			    	<div class="configLabel"><label>사용: </label></div><div style="float:left;"><div id="useYn"></div></div>		    	   	
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
			    	<div style="float:left;"><div id="searchDate"></div></div><div style="float:left;"><input type="button" id="btnSearchLog" value="검색"/></div>		    	
			    </div>
			    <div id="nmsLogGrid"></div>
			 </form>
	    </div>
	</div>
</div>
</body>
</html>