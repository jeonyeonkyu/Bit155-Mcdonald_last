<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!--css-->
<jsp:include page="/WEB-INF/user/common/head.jsp"></jsp:include>



<title>자유게시판</title>
</head>

<style>
.boardfreelist {
   background-color: #f5bd43;
}
/* 테이블 컬럼(맨위) 에다가 width 속성주기 */
.boardfreelist th:nth-child(1) {
   width: 7%
}

.boardfreelist th:nth-child(2) {
   widows: 44%;
}

.boardfreelist th:nth-child(3) {
   width: 12%
}

.boardfreelist th:nth-child(4) {
   width: 23%
}

.boardfreelist th:nth-child(5) {
   width: 7%
}

/* .boardfreelist th:nth-child(6) {
   width: 7%
} */
</style>

<body>

   <!-- header include-->
   <jsp:include page="../common/header.jsp"></jsp:include>

   <div class="board_free_list2">
      <h1 class="titDep1" style="padding-bottom: 20px; padding-top: 20px;">
         <strong>자유게시판</strong>
      </h1>
   </div>

   <p></p>


   <div class="container">
      <div class="row">
         <div class="col-sm-12 col-md-6 ">
            <div class="form-group d-flex align-items-center">
               <div class="col-sm-2" style="padding-left: 0">
                  <form action="BoardFree.b" name="list">
                     <select name="ps" class="form-control" onchange="submit()">
                        <c:set var="pagesize" value="${requestScope.pagesize}" />
                        <c:forEach var="i" begin="5" end="20" step="5">
                           <c:choose>
                              <c:when test="${pagesize == i}">
                                 <option value="${i}" selected>${i}</option>
                              </c:when>
                              <c:otherwise>
                                 <option value="${i}">${i}</option>
                              </c:otherwise>
                           </c:choose>
                        </c:forEach>
                     </select>
                  </form>
               </div>
               <label for="" style="margin-bottom: 0">개씩 보기</label>
            </div>
         </div>

         <div class="col-sm-12 col-md-6"
            style="border-left-color: rgb(241, 241, 241); text-align: end;">
            <div class="d-flex justify-content-end">
               <div class="col-sm-3">

                  <select id="selectBox" name = "selectBox" class="form-control">
                  <!--    <option selected>조건선택</option> -->
                     <option value="제목" selected>제목</option>
                     <option value="작성자">작성자</option>
                  </select>
               </div>

               <input style="width: 200px;" class="form-control" type="text"
                  id="search" name="search" placeholder="검색어를 입력하세요">

            </div>
         </div>
      </div>

      <!-- 게시판 리스트  -->
      <div class="row">
         <div class="col-md-12">
            <table class="table table-hover text-center">
               <thead>
                  <tr class="boardfreelist" style="text-align: center;">
                     <th>글번호</th>
                     <th>제목</th>
                     <th>작성자</th>
                     <th>작성일</th>
                     <th>조회수</th>
                     <!-- <th>좋아요</th> -->
                  </tr>
               </thead>
               <tbody id="tbody">

                  <c:set var="list" value="${requestScope.list}"></c:set>
                  <c:forEach var="board" items="${list}">
                     <tr>
                        <td>${board.f_code}</td>
                        <td align="left"><c:forEach var="i" begin="1"
                              end="${board.f_depth}" step="1">
                        &nbsp;&nbsp;&nbsp;
                     </c:forEach> <c:if test="${board.f_depth > 0}">
                              <!-- depth가 0보다 크다는것은 원본글이 아니다 -->
                              <img
                                 src="${pageContext.request.contextPath}/usercss/vendors/images/re.gif">
                           </c:if> <a
                           href="BoardFreeDetail.b?f_code=${board.f_code}&cp=${cpage}&ps=${pagesize}">
                              <c:choose>
                                 <c:when
                                    test="${board.f_title != null && fn:length(board.f_title) > 10}">
                              ${fn:substring(board.f_title, 0,10)}... 
                                          </c:when>
                                 <c:otherwise>
                              ${board.f_title}
                           </c:otherwise>
                              </c:choose>
                        </a></td>
                        <td>${board.f_writer}</td>
                        <td>${board.f_date}</td>
                        <td>${board.f_readnum}</td>
                        <%-- <td>${board.f_like}</td> --%>
                     </tr>
                  </c:forEach>
               </tbody>
            </table>

         </div>
      </div>



      <div class="row" style="border-color: #eee;">
         <div class="col-md-4"></div>
         <div class="col-md-5" style="text-align: center;">
            <ul class="pagination" style="margin: 0 auto">

               <!-- justify-content-end -->
               <c:if test="${cpage>1 }">
                  <li class="page-item"><a
                     href="BoardFree.b?cp=${cpage-1}&ps=${pagesize}" class="page-link">
                        <i class="fas fa-arrow-left"></i>
                  </a></li>
               </c:if>
               <c:forEach var="i" begin="1" end="${pagecount}" step="1">
                  <c:choose>
                     <c:when test="${cpage == i }">
                        <li class="page-item"><a href="" class="page-link"
                           style="color: red">${i}</a></li>
                     </c:when>
                     <c:otherwise>
                        <li class="page-item"><a
                           href="BoardFree.b?cp=${i}&ps=${pagesize}" class="page-link">${i}</a></li>
                     </c:otherwise>
                  </c:choose>
               </c:forEach>
               <c:if test="${cpage < pagecount}">
                  <li class="page-item"><a
                     href="BoardFree.b?cp=${cpage+1}&ps=${pagesize}" class="page-link">
                        <i class="fas fa-arrow-right"></i>
                  </a></li>
               </c:if>
            </ul>

         </div>

         <div class="col-md-2"></div>

         <c:choose>
            <c:when test="${id != null}">
               <div class="col-md-1" style="padding: 0px;">
                  <a href="BoardFreeRegisterPage.b" type="button"
                     class="btn btn-info" style="padding: 5px;"><b>글쓰기</b> </a>
            </c:when>
            <c:otherwise>

            </c:otherwise>
         </c:choose>
      </div>
   </div>


   <button onclick="topFunction()" id="myBtn" title="Go to top">Top</button>

   <jsp:include page="/WEB-INF/user/common/footer.jsp"></jsp:include>


   <jsp:include page="/WEB-INF/user/common/script.jsp"></jsp:include>

   <script
   src="${pageContext.request.contextPath}/usercss/assets/js/weather.js"></script> 


   <script src='https://kit.fontawesome.com/a076d05399.js'></script>
   <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

   
<script>

$(function(){
   
   var mybutton = document.getElementById("myBtn");

   // When the user scrolls down 20px from the top of the document, show the button
   window.onscroll = function() {
      scrollFunction()
   };

   function scrollFunction() {
      if (document.body.scrollTop > 20
            || document.documentElement.scrollTop > 20) {
         mybutton.style.display = "block";
      } else {
         mybutton.style.display = "none";
      }
   }

   // When the user clicks on the button, scroll to the top of the document
   function topFunction() {
      document.body.scrollTop = 0;
      document.documentElement.scrollTop = 0;
   }
})
   </script>
   <script>
   //비동기 검색기능

      var keyword = $("#selectBox option:selected").val()
      $('#selectBox').change(function() {
         keyword = $("#selectBox option:selected").val()
         console.log(keyword);
         console.log("sss");
      })

      $('#search').keyup(
            function() {
            console.log("keyup 했어요")
               if (keyword == "제목") { console.log(keyword);
                  $.ajax({
                     url : "search.ua",
                     type : 'POST',
                     dataType : "json",
                     data : {
                        ftitle : $("#search").val()
                     },
                     success : function(data) {
                       
                        $('#tbody').empty();
//                         if($("#search").val() != ""){
                           
                        $.each(data, function(key, value) {
                        
                        /*    <td id="e" align="center"><img
                     src="upload/${list2.filename}"
                     style="width: 30px; heigfht: 30px;"></td> */
      
                     
                           let startable = $("#tbody");
                              startable += "<tr>";
                                 startable += "<td>" + value.f_code + "</td>";
                                 startable += "<td>"+"<a href='BoardFreeDetail.b?f_code="+ value.f_code +"&cp=${cpage}&ps=${pagesize}'>" + value.f_title + "</a>"+"</td>";
                                 startable += "<td>" + value.f_writer + "</td>";
                                 startable += "<td>" + value.f_date + "</td>";
                                 startable += "<td>" + value.f_readnum + "</td>";
/*                               startable += "<td>" + value.f_like + "</td>"; */
                              startable += "</tr>";

                           startable += "</table>";
                            $('#tbody').append(startable);
                        });
                     }

                     });
               } else {
                  $.ajax({
                     url : "search.ua",
                     type : 'POST',
                     dataType : "json",
                     data : {
                        fwriter : $("#search").val()
                     },
                     success : function(data) {
                        $('#tbody').empty();
                        if($("#search").val() != ""){
                        
                        $.each(data, function(key, value) {
                           let startable = "#tbody";
                              startable += "<tr>";
                              startable += "<td>" + value.f_code + "</td>";
                              startable += "<td>"+"<a href='BoardFreeDetail.b?f_code="+ value.f_code +"&cp=${cpage}&ps=${pagesize}'>" + value.f_title + "</a>"+"</td>";
                              startable += "<td>" + value.f_writer + "</td>";
                              startable += "<td>" + value.f_date + "</td>";
                              startable += "<td>" + value.f_readnum + "</td>";
/*                                  startable += "<td>" + value.f_like + "</td>"; */
                           startable += "</tr>";

                           startable += $("#tbody");
                           $('#tbody').append(startable); 

                        });
                     }
                        }

                  })

               };
               });

</script>

</body>
</html>
