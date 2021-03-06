package kr.or.mc.user.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;

import kr.or.mc.admin.service.member.MemberListService;
import kr.or.mc.admin.service.member.MemberUpdateService;
import kr.or.mc.common.action.Action;
import kr.or.mc.common.action.ActionForward;
import kr.or.mc.user.service.board.FreeDeleteService;
import kr.or.mc.user.service.board.FreeDetailService;
import kr.or.mc.user.service.board.FreeFileDownload;
import kr.or.mc.user.service.board.FreeListService;
import kr.or.mc.user.service.board.FreeReRegister;
import kr.or.mc.user.service.board.FreeReRegisterPage;
import kr.or.mc.user.service.board.FreeRegisterService;
import kr.or.mc.user.service.board.FreeUpdatePageService;
import kr.or.mc.user.service.board.FreeUpdateService;
import kr.or.mc.user.service.board.NoticeDeleteService;
import kr.or.mc.user.service.board.NoticeDetailService;
import kr.or.mc.user.service.board.NoticeListService;
import kr.or.mc.user.service.board.NoticeRegisterService;
import kr.or.mc.user.service.board.NoticeUpdateService;
import kr.or.mc.user.service.board.NoticeUpdatePageService;

@WebServlet("*.b")
public class UserBoardController extends HttpServlet {

   public UserBoardController() {
      super();
   }

   private void doProcess(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {

      String requestURI = request.getRequestURI();
      String contextPath = request.getContextPath();
      String url_Command = requestURI.substring(contextPath.length());

      Action action = null;
      ActionForward forward = null;
      System.out.println(" url_Command : " + url_Command);

      if (url_Command.equals("/BoardNotice.b")) { // 공지사항 목록 뿌려주는 로직
         action = new NoticeListService();
         forward = action.execute(request, response);
      } else if (url_Command.equals("/BoardNoticeDetail.b")) { // 공지사항 상세 뿌려주는 로직
         action = new NoticeDetailService();
         forward = action.execute(request, response);
      } else if (url_Command.equals("/BoardNoticeRegisterPage.b")) { // 공지사항 등록 페이지로 가는거
         forward = new ActionForward();
         forward.setPath("/WEB-INF/user/comm/Mcdonald_board_notice_writer.jsp");
      } else if (url_Command.equals("/BoardNoticeRegister.b")) { // 공지사항 등록
         action = new NoticeRegisterService();
         forward = action.execute(request, response);
      } else if (url_Command.equals("/BoardNoticeUpdatePage.b")) { // 공지사항 수정 페이지로 가는거
         action = new NoticeUpdatePageService();
         forward = action.execute(request, response);
      } else if (url_Command.equals("/BoardNoticeUpdate.b")) { // 공지사항 수정
         action = new NoticeUpdateService();
         forward = action.execute(request, response);
      } else if (url_Command.equals("/BoardNoticeDelete.b")) { // 공지사항 삭제
         action = new NoticeDeleteService();
         forward = action.execute(request, response);
      } else if (url_Command.equals("/BoardFree.b")) { // 자유게시판 목록 뿌려주는 로직
         action = new FreeListService();
         forward = action.execute(request, response);
      } else if (url_Command.equals("/BoardFreeDetail.b")) { // 자유게시판 상세 뿌려주는 로직
         action = new FreeDetailService();
         forward = action.execute(request, response);
      } else if (url_Command.equals("/BoardFreeRegisterPage.b")) { // 자유게시판 등록 페이지 이동
         forward = new ActionForward();
         forward.setPath("/WEB-INF/user/comm/Mcdonald_board_free_writer.jsp");
      } else if (url_Command.equals("/BoardFreeRegister.b")) { // 자유게시판 등록
         action = new FreeRegisterService();
         forward = action.execute(request, response);
      } else if (url_Command.equals("/BoardFreeUpdatePage.b")) { // 자유게시판 수정 페이지 이동
         action = new FreeUpdatePageService();
         forward = action.execute(request, response);
      } else if (url_Command.equals("/BoardFreeUpdate.b")) { // 자유게시판 수정
         action = new FreeUpdateService();
         forward = action.execute(request, response);
      } else if (url_Command.equals("/BoardFreeDelete.b")) { // 자유게시판 삭제
         action = new FreeDeleteService();
         forward = action.execute(request, response);
      } else if (url_Command.equals("/BoardFreeReRegisterPage.b")) { // 자유게시판 답변 페이지 이동
         action = new FreeReRegisterPage();
         forward = action.execute(request, response);
      } else if (url_Command.equals("/BoardFreeReRegister.b")) { // 자유게시판 답변 로직
         action = new FreeReRegister();
         forward = action.execute(request, response);
      }else if (url_Command.equals("/BoardFreeFileDownload.b")) { // 자유게시판 첨부파일 다우놀드
         action = new FreeFileDownload();
           forward = action.execute(request, response);
           }
          

      if (forward != null) {
         RequestDispatcher dis = request.getRequestDispatcher(forward.getPath());
         dis.forward(request, response);
      }

   }

   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      doProcess(request, response);

   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      doProcess(request, response);
   }

}