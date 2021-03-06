package kr.or.mc.user.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import kr.or.mc.common.dto.BasketDTO;
import kr.or.mc.common.dto.BoardFreeDTO;
import kr.or.mc.common.dto.BoardNoticeDTO;
import kr.or.mc.common.dto.MemberDTO;
import kr.or.mc.common.dto.NutritionDTO;
import kr.or.mc.common.dto.OrderDetailDTO;
import kr.or.mc.common.dto.OrdersDTO;
import kr.or.mc.common.dto.ProductDTO;
import kr.or.mc.common.dto.ReplyDTO;
import kr.or.mc.common.dto.StoreDTO;
import kr.or.mc.common.utils.DB_Close;

public class UserDAO {

	public static UserDAO userDao;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private int result;
	DataSource ds = null;

	// DB연결
	public UserDAO() {
		try {
			Context context = new InitialContext();
			ds = (DataSource) context.lookup("java:comp/env/jdbc/oracle");// java:comp/env/ + name
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	// 싱글톤
	public static synchronized UserDAO getInstance() {
		if (userDao == null) {
			userDao = new UserDAO();
		}
		return userDao;
	}

	// 공지게시판 목록 뿌리기
	public List<BoardNoticeDTO> NoticeList() {
		System.out.println("노티스리스트");
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BoardNoticeDTO> list = null;

		try {
			conn = ds.getConnection();
			String sql = "select * from board_notice"; // while
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			list = new ArrayList<BoardNoticeDTO>();

			while (rs.next()) {
				BoardNoticeDTO BoardNotice = new BoardNoticeDTO();
				BoardNotice.setN_code(rs.getInt("n_code"));
				BoardNotice.setN_title(rs.getString("n_title"));
				BoardNotice.setN_content(rs.getString("n_content"));
				BoardNotice.setN_writer(rs.getString("n_writer"));
				BoardNotice.setN_write_date(rs.getString("n_write_date"));
				BoardNotice.setN_read_num(rs.getInt("n_read_num"));
				list.add(BoardNotice);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				pstmt.close();
				rs.close();
				conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return list;

	}

	// 로그인
	// 회원가입
	public int MemRegister(MemberDTO memberdto) {
		Connection conn = null;// 추가
		try {
			conn = ds.getConnection();
			// System.out.println(m_id+"/"+password+"/"+name+"/"+email+"/"+post_code+"/"+address+"/"+phone);
			System.out.println(memberdto.toString() + "투스트링1");
			String sql = "insert into member(m_id,password,name,email,post_code,address,phone) values(?,?,?,?,?,?,?)";

			pstmt = conn.prepareStatement(sql);
			/*
			 * pstmt.setString(1, m_id); pstmt.setString(2, password); pstmt.setString(3,
			 * name); pstmt.setString(4, email); pstmt.setString(5, post_code);
			 * pstmt.setString(6, address); pstmt.setString(7, phone);
			 */
			pstmt.setString(1, memberdto.getM_id());
			pstmt.setString(2, memberdto.getPassword());
			pstmt.setString(3, memberdto.getName());
			pstmt.setString(4, memberdto.getEmail());
			pstmt.setString(5, memberdto.getPost_code());
			pstmt.setString(6, memberdto.getAddress());
			pstmt.setString(7, memberdto.getPhone());

			System.out.println(memberdto.toString() + "투스트링2");
			result = pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Insert : " + e.getMessage());
		} finally {
			DB_Close.close(pstmt);
			try {
				DB_Close.close(conn); // 반환하기
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	// 로그인 아이디 비밀번호 확인
	public int login(String userId, String userPw) {
		System.out.println("여기오나?");
		System.out.println(userId + userPw);

		Connection conn = null;
		try {
			conn = ds.getConnection();
			String sql = "";

			if (!userId.equals("admin")) {
				sql = "select password from member where m_id = ?";
			} else {
				sql = "select password from admin where a_id = ?";
			}
			// String sql = "select password from member where m_id = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				if (rs.getString("password").equals(userPw)) {
					return 1;
				} else {
					return 0;
				}
			}
			conn.close(); //
		} catch (Exception e) {
			System.err.println("login SQLException error : " + e.getMessage());
		} finally {
			DB_Close.close(rs);
			DB_Close.close(pstmt);
			DB_Close.close(conn); // 반환

		}
		return -1;
	}

	// 공지사항 상세보기
	public BoardNoticeDTO BoardNoticeDetail(int n_code) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardNoticeDTO boardNoticeDTO = new BoardNoticeDTO();
		try {
			conn = ds.getConnection();
			String sql = "select * from board_notice where n_code = ? ";
			// n_code, n_title, to_char(n_write_date, 'YYYY-MM-DD') as n_write_date,
			// n_read_num, n_content, from board_notice where n_code = ?
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, n_code);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				boardNoticeDTO.setN_code(rs.getInt("n_code"));
				boardNoticeDTO.setN_title(rs.getString("n_title"));
				boardNoticeDTO.setN_write_date(rs.getString("n_write_date"));
				boardNoticeDTO.setN_read_num(rs.getInt("n_read_num"));
				boardNoticeDTO.setN_content(rs.getString("n_content"));
			}

		} catch (Exception e) {
			System.out.println(" boardNoticeDto : " + e.getMessage());
		} finally {
			try {
				DB_Close.close(pstmt);
				DB_Close.close(rs);
				DB_Close.close(conn); // 반환
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return boardNoticeDTO;
	}

	// 회원정보 수정
	public int MemEdit(MemberDTO memberdto) {
		Connection conn = null;// 추가
		try {
			conn = ds.getConnection();

			System.out.println(memberdto.toString() + "투스트링1");
			String sql = " member set password = ?, name =?, email = ?, post_code = ?, address = ?, phone = ? WHERE m_id = ?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, memberdto.getPassword());
			pstmt.setString(2, memberdto.getName());
			pstmt.setString(3, memberdto.getEmail());
			pstmt.setString(4, memberdto.getPost_code());
			pstmt.setString(5, memberdto.getAddress());
			pstmt.setString(6, memberdto.getPhone());
			pstmt.setString(7, memberdto.getM_id());

			result = pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("MemEdit : " + e.getMessage());
		} finally {
			DB_Close.close(pstmt);
			try {
				DB_Close.close(conn); // 반환하기
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	// 회원정보 목록 뿌려주기위한...
	public MemberDTO MemDetail(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		MemberDTO memberDTO = new MemberDTO();
		try {
			conn = ds.getConnection();
			String sql = "select * from member where m_id = ?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, id);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				memberDTO.setM_id(rs.getString("m_id"));
				memberDTO.setPassword(rs.getString("password"));
				memberDTO.setName(rs.getString("name"));
				memberDTO.setEmail(rs.getString("email"));
				memberDTO.setPost_code(rs.getString("post_code"));
				String[] temp = rs.getString("address").split("/");
				memberDTO.setAddress(temp[0]);
				memberDTO.setAddress_detail(temp[1]);
				memberDTO.setPhone(rs.getString("phone"));
			}

		} catch (Exception e) {
			System.out.println("MemDetail :" + e.getMessage());
		} finally {
			try {
				DB_Close.close(pstmt);
				DB_Close.close(rs);
				DB_Close.close(conn); // 반환
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return memberDTO;
	}

	// 공지사항 작성하기
	public int BoardNoticeWriter(BoardNoticeDTO noticedto) {
		Connection conn = null;// 추가

		try {
			conn = ds.getConnection();

			String sql = "insert into board_notice(n_code,n_title,n_content,n_writer,n_write_date,n_read_num) values(board_notice_sq.nextval,?,?,'admin',sysdate,0)";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, noticedto.getN_title());
			pstmt.setString(2, noticedto.getN_content());
			result = pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("BoardNoticeWriter : " + e.getMessage());
		} finally {
			DB_Close.close(pstmt);
			try {
				DB_Close.close(conn); // 반환하기
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	// 메뉴디테일 - 상세보기(상품)
	public ProductDTO MenuDetail(int product_code) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ProductDTO productDto = new ProductDTO();
		try {
			conn = ds.getConnection();
			String sql = "select * from product where product_code = ?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, product_code);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				productDto.setProduct_code(rs.getInt("product_code"));
				productDto.setProduct_category(rs.getString("product_category"));
				productDto.setProduct_name(rs.getString("product_name"));
				productDto.setProduct_price(rs.getInt("product_price"));
				productDto.setProduct_kind(rs.getString("product_kind"));
				productDto.setProduct_image(rs.getString("product_image"));
			}

		} catch (Exception e) {
			System.out.println(" MenuDetail : " + e.getMessage());
		} finally {
			try {
				DB_Close.close(pstmt);
				DB_Close.close(rs);
				DB_Close.close(conn); // 반환
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return productDto;
	}

	// 메뉴디테일 - 상세보기(영양)
	public NutritionDTO MenuDetailNut(int product_code) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		NutritionDTO nutritionDto = new NutritionDTO();
		try {
			conn = ds.getConnection();
			String sql = "select n.nutrition_code, n.weight, n.calorie, n.sugar, n.protein, n.fat, n.natrium, n.caffeine\r\n"
					+ "from product p join nutrition n on p.nutrition_code = n.nutrition_code\r\n"
					+ "where p.product_code = ?\r\n" + "";
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, product_code);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				nutritionDto.setNutrition_code(rs.getInt("nutrition_code"));
				nutritionDto.setWeight(rs.getInt("weight"));
				nutritionDto.setCalorie(rs.getInt("calorie"));
				nutritionDto.setSugar(rs.getInt("sugar"));
				nutritionDto.setProtein(rs.getInt("protein"));
				nutritionDto.setFat(rs.getInt("fat"));
				nutritionDto.setNatrium(rs.getInt("natrium"));
				nutritionDto.setCaffeine(rs.getInt("caffeine"));
			}

		} catch (Exception e) {
			System.out.println(" MenuDetailNut : " + e.getMessage());
		} finally {
			try {
				DB_Close.close(pstmt);
				DB_Close.close(rs);
				DB_Close.close(conn); // 반환
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return nutritionDto;
	}

	// 자유게시판 목록 뿌리기
	public List<BoardFreeDTO> FreeList(int cpage, int pagesize) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BoardFreeDTO> list = null;

		try {
			conn = ds.getConnection();

			String sql = "select * from \r\n"
					+ "(select rownum rn , f_code , f_title , f_content, f_writer, to_char(f_date, 'YYYY-MM-DD') as f_date , f_readnum,  f_file_upload,\r\n"
					+ "f_refer , f_depth , f_step\r\n"
					+ "from ( SELECT * FROM board_free ORDER BY f_refer DESC , f_step ASC)\r\n"
					+ "where rownum <= ?)\r\n" + "where rn >= ?\r\n" + "";

			pstmt = conn.prepareStatement(sql);

			int start = cpage * pagesize - (pagesize - 1); // 1*5 -(5-1) = 1
			int end = cpage * pagesize; // 1 * 5 = 5

			pstmt.setInt(1, end);
			pstmt.setInt(2, start);

			rs = pstmt.executeQuery();
			list = new ArrayList<BoardFreeDTO>();

			while (rs.next()) {
				BoardFreeDTO BoardFree = new BoardFreeDTO();
				BoardFree.setF_code(rs.getInt("f_code"));
				BoardFree.setF_title(rs.getString("f_title"));
				BoardFree.setF_writer(rs.getString("f_writer"));
				BoardFree.setF_date(rs.getString("f_date"));
				BoardFree.setF_readnum(rs.getInt("f_readnum"));

				BoardFree.setF_refer(rs.getInt("f_refer"));
				BoardFree.setF_depth(rs.getInt("f_depth"));
				BoardFree.setF_step(rs.getInt("f_step"));
				list.add(BoardFree);
			}

		} catch (Exception e) {
			System.out.println("FreeList 오류 " + e.getMessage());
		} finally {
			try {
				pstmt.close();
				rs.close();
				conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return list;
	}

	// 공지사항 삭제
	public int ProductDelete(int n_code) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		try {
			conn = ds.getConnection();
			String sql = "delete from board_notice where n_code = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, n_code);
			result = pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("ProductDelete 오류 :" + e.getMessage());
		} finally {
			try {
				DB_Close.close(pstmt);
				DB_Close.close(conn);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	// 공지사항 수정
	public int NoticeUpdate(BoardNoticeDTO noticedto) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ds.getConnection();
			String sql = "update board_notice set n_title = ?, n_content =? where n_code = ?";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, noticedto.getN_title());
			pstmt.setString(2, noticedto.getN_content());
			pstmt.setInt(3, noticedto.getN_code());

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				DB_Close.close(pstmt);
				DB_Close.close(conn); // 반환하기
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	// 자유게시판 전체 게시글 개수
	public int totalBoardCount() {
		Connection conn = null;
		PreparedStatement pstmt = null; //
		ResultSet rs = null;
		int totalcount = 0;
		try {
			conn = ds.getConnection();
			String sql = "select count(*) cnt from board_free"; //
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				totalcount = rs.getInt("cnt"); //
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				pstmt.close();
				rs.close();
				conn.close(); //
			} catch (Exception e2) {
				System.out.println(e2.getMessage());
			}
		}
		return totalcount;
	}

	// 자유게시판 등록
	public int FreeRegister(BoardFreeDTO boardFreeDto) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int row = 0;
		try {
			conn = ds.getConnection();
			String sql = "insert into board_free(f_code,f_title,f_content,f_writer,f_date,f_readnum,f_file_upload,f_refer)"
					+ "values(board_free_sq.nextval,?,?,?,sysdate,0,?,?)";
			// refer까지.. step, depth는 XX

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, boardFreeDto.getF_title());
			pstmt.setString(2, boardFreeDto.getF_content());
			pstmt.setString(3, boardFreeDto.getF_writer());
			pstmt.setString(4, boardFreeDto.getF_file_upload());

			// refer
			int refermax = getMaxRefer();
			int refer = refermax + 1;

			pstmt.setInt(5, refer);

			row = pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("FreeRegister : " + e.getMessage());
		} finally {
			try {
				pstmt.close();
				conn.close(); //
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return row;
	}

	// 자유게시판(계층형)서 refer 최대값 추출
	public int getMaxRefer() {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int refer_max = 0;
		try {
			conn = ds.getConnection(); //
			String sql = "select nvl(max(f_refer),0) from board_free";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				refer_max = rs.getInt(1);
			}
		} catch (Exception e) {
			System.out.println("getMaxRefer : " + e.getMessage());
		} finally {
			try {
				pstmt.close();
				rs.close();
				conn.close(); // �
			} catch (Exception e) {

			}
		}
		return refer_max;

	}

	// 자유게시판(계층형) 상세 페이지
	public BoardFreeDTO FreeDetail(int f_code) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardFreeDTO boardFreeDto = null;

		try {
			conn = ds.getConnection();
			String sql = "select f_code, f_title, f_content, f_writer, to_char(f_date, 'YYYY-MM-DD') as f_date, f_readnum, f_file_upload, f_refer, f_depth, f_step\r\n"
					+ "from board_free\r\n" + "where f_code = ?\r\n" + "";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, f_code);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				String f_title = rs.getString("f_title");
				String f_content = rs.getString("f_content");
				String f_writer = rs.getString("f_writer");
				String f_date = rs.getString("f_date");
				int f_readnum = rs.getInt("f_readnum");
				String f_file_upload = rs.getString("f_file_upload");

				int f_refer = rs.getInt("f_refer");
				int f_step = rs.getInt("f_depth");
				int f_depth = rs.getInt("f_step");

				boardFreeDto = new BoardFreeDTO();
				boardFreeDto.setF_code(f_code);
				boardFreeDto.setF_title(f_title);
				boardFreeDto.setF_content(f_content);
				boardFreeDto.setF_writer(f_writer);
				boardFreeDto.setF_date(f_date);
				boardFreeDto.setF_readnum(f_readnum);
				boardFreeDto.setF_file_upload(f_file_upload);
				boardFreeDto.setF_refer(f_refer);
				boardFreeDto.setF_step(f_step);
				boardFreeDto.setF_depth(f_depth);
			}

		} catch (Exception e) {
			System.out.println("FreeDetail: " + e.getMessage());
		} finally {
			try {
				pstmt.close();
				rs.close();
				conn.close();//
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return boardFreeDto;
	}

	// 자유게시판 조회수 증가
	public boolean getReadNum(int f_code) {
		// update jspboard set readnum = readnum + 1 where idx=?
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean result = false;
		try {
			conn = ds.getConnection();
			String sql = "update board_free set f_readnum = f_readnum + 1 where f_code=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, f_code);

			int row = pstmt.executeUpdate();
			if (row > 0) {
				result = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (Exception e) {

			}
		}
		return result;
	}

	// 자유게시판 - 수정
	public int FreeUpdate(BoardFreeDTO boardFreeDto) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ds.getConnection();

			String sql = "update board_free set f_title  = ?, f_content =?, f_writer = ?, f_file_upload = ? WHERE f_code = ?";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, boardFreeDto.getF_title());
			pstmt.setString(2, boardFreeDto.getF_content());
			pstmt.setString(3, boardFreeDto.getF_writer());
			pstmt.setString(4, boardFreeDto.getF_file_upload());
			pstmt.setInt(5, boardFreeDto.getF_code());

			result = pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("FreeUpdate : " + e.getMessage());
		} finally {
			try {
				pstmt.close();
				conn.close(); // 받환하기
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	// 자유게시판 - 삭제
	public int FreeDelete(int f_code) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "delete from board_free where f_code=?";
		int row = 0;
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, f_code);
			row = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("FreeDelete : " + e.getMessage());
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return row;
	}

	// 자유게시판 답변
	public int FreeReRegister(BoardFreeDTO boardFreeDto) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = 0;

		try {
			conn = ds.getConnection();

			int f_code = boardFreeDto.getF_code();

			String f_title = boardFreeDto.getF_title();
			String f_content = boardFreeDto.getF_content();
			String f_writer = boardFreeDto.getF_writer();
			String f_file_upload = boardFreeDto.getF_file_upload();

			int filesize = 0;

			String refer_depth_step_sal = "select f_refer , f_depth , f_step from board_free where f_code=?";

			String step_update_sql = "select nvl(min(f_step), 0) f_step from board_free where f_refer=? and f_step > ? and f_depth <= ?";

			String rewrite_sql = "insert into board_free(f_code,f_title,f_content,f_writer,f_date,f_readnum,f_file_upload,f_refer,f_depth,f_step)"
					+ " values(board_free_sq.nextval,?,?,?,sysdate,0,?,?,?,?)";

			pstmt = conn.prepareStatement(refer_depth_step_sal);
			pstmt.setInt(1, f_code);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				int f_refer = rs.getInt("f_refer");
				int f_step = rs.getInt("f_step");
				int f_depth = rs.getInt("f_depth");

				pstmt = conn.prepareStatement(step_update_sql);
				pstmt.setInt(1, f_refer);
				pstmt.setInt(2, f_step);
				pstmt.setInt(3, f_depth);
				pstmt.executeUpdate();
				rs = pstmt.executeQuery();
				// filename,filesize,refer,depth,step

				if (rs.next()) {
					f_step = rs.getInt("f_step");
					if (f_step == 0) {
						String maxStep = "select max(f_step)+1 maxStep from board_free where f_refer=?";
						pstmt = conn.prepareStatement(maxStep);
						pstmt.setInt(1, f_refer); // 원본글 ref
						rs = pstmt.executeQuery();
						if (rs.next()) {
							f_step = rs.getInt("maxStep");
						}
					} else {
						String update_step = "update board_free set f_step=f_step+1 where f_refer=? and f_step >= ? ";
						pstmt = conn.prepareStatement(update_step);
						pstmt.setInt(1, f_refer); // 원본글 ref
						pstmt.setInt(2, f_step);
						pstmt.executeUpdate();
					}
				}

				// filename, filesize, refer, depth, step
				pstmt = conn.prepareStatement(rewrite_sql); // 컴파일
				pstmt.setString(1, f_title);
				pstmt.setString(2, f_content);
				pstmt.setString(3, f_writer);
				pstmt.setString(4, f_file_upload);

				// 답변
				pstmt.setInt(5, f_refer);
				pstmt.setInt(6, f_depth + 1);
				pstmt.setInt(7, f_step + 1);

				int row = pstmt.executeUpdate();
				if (row > 0) {
					result = row;
				} else {
					result = -1;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				rs.close();
				conn.close();// 반환
			} catch (Exception e) {

			}
		}

		return result;
	}

	// 아이디 중복 체크
	public int checkId(String id) {
		System.out.println("여기오나?");
		System.out.println(id);

		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "select m_id from member where m_id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				if (rs.getString("m_id").equals(id)) {
					return 1;
				} else {
					return 0;
				}
			}
			conn.close(); //
		} catch (Exception e) {
			System.err.println("idcheck SQLException error : " + e.getMessage());
			System.err.println("");
		} finally {
			DB_Close.close(rs);
			DB_Close.close(pstmt);

		}
		return -1;
	}

	// orderHistroy 에서 주문조회하기위한 함수
	public List<OrdersDTO> OrderHistoryView(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		List<OrdersDTO> list = new ArrayList<OrdersDTO>();
		try {
			conn = ds.getConnection();
			String sql = "select o.order_code, o.o_id, o.s_name, o.payment_method, o.payment_price, to_char(payment_date, 'YYYY-MM-DD HH24:MI:SS') as payment_date, m.address\r\n"
					+ "from orders o join member m on o.o_id = m.m_id where m.m_id = ? \r\n"
					+ "order by o.order_code desc" + "";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, id);

			// set 할수있게 dto 를 만들어봅시다.
			rs = pstmt.executeQuery();
			while (rs.next()) {

				OrdersDTO ordersDTO = new OrdersDTO();

				ordersDTO.setOrder_code(rs.getInt("order_code"));
				ordersDTO.setO_id(rs.getString("o_id"));
				ordersDTO.setS_name(rs.getString("s_name"));

				ordersDTO.setPayment_method(rs.getString("payment_method"));
				ordersDTO.setPayment_price(rs.getInt("payment_price"));
				ordersDTO.setPayment_date(rs.getString("payment_date"));
				String[] temp = rs.getString("address").split("/");
				ordersDTO.setAddress(temp[0]);
				ordersDTO.setAddress_detail(temp[1]);

				list.add(ordersDTO);

			}

		} catch (Exception e) {
			System.out.println("오류 :" + e.getMessage());
		} finally {
			try {
				DB_Close.close(pstmt);
				DB_Close.close(rs);
				DB_Close.close(conn); // 반환
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return list;
	}

	// 주문관리 - 상세보기(주문 상세 + 상품)
	public List<OrderDetailDTO> OrderDetailProductView(int order_code) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<OrderDetailDTO> list = new ArrayList<OrderDetailDTO>();
		try {
			conn = ds.getConnection();
			String sql = "select p.product_image, p.product_name, od.order_amount \r\n"
					+ "from order_detail od join product p on od.product_code = p.product_code\r\n"
					+ "where order_code = ? \r\n" + "";
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, order_code);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				OrderDetailDTO orderDetailDto = new OrderDetailDTO();
				orderDetailDto.setOrder_code(order_code);
				orderDetailDto.setProduct_image(rs.getString("product_image"));
				orderDetailDto.setProduct_name(rs.getString("product_name"));
				orderDetailDto.setOrder_amount(rs.getInt("order_amount"));

				list.add(orderDetailDto);
			}

		} catch (Exception e) {
			System.out.println(" OrderDetailProduct : " + e.getMessage());
		} finally {
			try {
				DB_Close.close(pstmt);
				DB_Close.close(rs);
				DB_Close.close(conn); // 반환
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return list;
	}

	// 주문 페이지 리스트 띄우기(버거 & 세트) : 우철이가 만지는거
	public List<ProductDTO> getProductList(String product_category) {
		// 리넡할 객체 선언
		List<ProductDTO> productList = new ArrayList<ProductDTO>();
		Connection conn = null;

		try {
			conn = ds.getConnection();
			String sql = "\r\n"
					+ "select p.product_code, p.product_name, p.product_price, p.product_kind, p.product_image, p.product_category, n.calorie\r\n"
					+ "from product p join nutrition n on p.nutrition_code = n.nutrition_code\r\n"
					+ "where product_category = ?" + "";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, product_category);
			rs = pstmt.executeQuery();

			System.out.println(product_category);

			while (rs.next()) {
				ProductDTO productDto = new ProductDTO();
				productDto.setProduct_code(rs.getInt("product_code"));
				productDto.setProduct_name(rs.getString("product_name"));
				productDto.setProduct_price(rs.getInt("product_price"));
				productDto.setProduct_kind(rs.getString("product_kind"));
				productDto.setProduct_category(rs.getString("product_category"));
				productDto.setProduct_image(rs.getString("product_image"));
				productDto.setNutrition_code(rs.getInt("calorie"));
				// 칼로리 담을 dto 다시만들기 싫어서 임시로 Nutrition_code에 담음

				productList.add(productDto);

			}

		} catch (Exception e) {
			e.printStackTrace();
			productList = null;
		} finally {
			// 자원 반납
			DB_Close.close(rs);
			DB_Close.close(pstmt);
			DB_Close.close(conn);

			return productList;
		}
	}

	// 공지게시판 조회수
	public boolean getNoticeReadNum(int n_code) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean result = false;
		try {
			conn = ds.getConnection();
			String sql = "update board_notice set n_read_num = n_read_num+1 where n_code=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, n_code);
			int row = pstmt.executeUpdate();
			if (row > 0) {
				result = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (Exception e) {

			}

		}
		return result;

	}

	// 매장정보 가져오기
	public List<StoreDTO> getSelectShop(String s_name) {
		// 리턴할 객체 선언
		List<StoreDTO> InfoShop = new ArrayList<StoreDTO>();
		Connection conn = null;

		try {
			conn = ds.getConnection();
			String sql = "select s_name, a_id, s_address, s_phone_number, s_business_hour from store where s_name = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, s_name);
			rs = pstmt.executeQuery();

			System.out.println(s_name);

			while (rs.next()) {
				StoreDTO StoreDto = new StoreDTO();
				StoreDto.setS_name(rs.getNString("s_name"));
				StoreDto.setA_id(rs.getString("a_id"));
				StoreDto.setS_address(rs.getString("s_address"));
				StoreDto.setS_phone_number(rs.getString("s_phone_number"));
				StoreDto.setS_business_hour(rs.getString("s_business_hour"));

				InfoShop.add(StoreDto);

			}

		} catch (Exception e) {
			e.printStackTrace();
			InfoShop = null;
		} finally {
			// 자원 반납
			DB_Close.close(rs);
			DB_Close.close(pstmt);
			DB_Close.close(conn);

		}
		return InfoShop;
	}

	// 장바구니 출력(모두)
	public List<BasketDTO> OrderCartList(String b_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BasketDTO> list = new ArrayList<BasketDTO>();
		try {
			conn = ds.getConnection();
			String sql = "select b.basket_code, b.b_id, b.product_code, b.s_name, b.amount, b.total_product_price, m.address, p.product_image,\r\n"
					+ "p.product_name, p.product_kind\r\n"
					+ "from basket b join product p on b.product_code = p.product_code\r\n"
					+ "              join member m on b_id = m_id \r\n" + "where b_id = ?" + "";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, b_id);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				BasketDTO basketDto = new BasketDTO();
				basketDto.setBasket_code(rs.getInt("basket_code"));
				basketDto.setB_id(rs.getString("b_id"));
				basketDto.setProduct_code(rs.getInt("product_code"));
				basketDto.setS_name(rs.getString("s_name"));
				basketDto.setAmount(rs.getInt("amount"));
				basketDto.setTotal_product_price(rs.getInt("total_product_price"));
				basketDto.setAddress(rs.getString("address"));
				basketDto.setProduct_image(rs.getString("product_image"));
				basketDto.setProduct_name(rs.getString("product_name"));
				basketDto.setProduct_kind(rs.getString("product_kind"));
				System.out.println(" 진짜화나나 야밤에 새벽1시에  : " + basketDto);
				list.add(basketDto);
			}

		} catch (Exception e) {
			System.out.println("OrderCartList: " + e.getMessage());
		} finally {
			try {
				DB_Close.close(pstmt);
				DB_Close.close(rs);
				DB_Close.close(conn); // 반환
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return list;
	}

	// 장바구니 출력(하나씩) : 단품 or 세트 하나만 선태했을때
	public List<BasketDTO> OrderCartListOne(String b_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BasketDTO> list = new ArrayList<BasketDTO>();
		try {
			conn = ds.getConnection();
			String sql = "select * from\r\n" + "(\r\n"
					+ "select b.basket_code, b.b_id, b.product_code, b.s_name, b.amount, b.total_product_price, m.address, p.product_image,\r\n"
					+ "p.product_name, p.product_kind\r\n"
					+ "from basket b join product p on b.product_code = p.product_code\r\n"
					+ "              join member m on b_id = m_id \r\n" + "where b_id = ?\r\n"
					+ "order by basket_code desc)\r\n" + "where rownum = 1\r\n" + "";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, b_id);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				BasketDTO basketDto = new BasketDTO();
				basketDto.setBasket_code(rs.getInt("basket_code"));
				basketDto.setB_id(rs.getString("b_id"));
				basketDto.setProduct_code(rs.getInt("product_code"));
				basketDto.setS_name(rs.getString("s_name"));
				basketDto.setAmount(rs.getInt("amount"));
				basketDto.setTotal_product_price(rs.getInt("total_product_price"));
				basketDto.setAddress(rs.getString("address"));
				basketDto.setProduct_image(rs.getString("product_image"));
				basketDto.setProduct_name(rs.getString("product_name"));
				basketDto.setProduct_kind(rs.getString("product_kind"));
				list.add(basketDto);
			}

		} catch (Exception e) {
			System.out.println("OrderCartList: " + e.getMessage());
		} finally {
			try {
				DB_Close.close(pstmt);
				DB_Close.close(rs);
				DB_Close.close(conn); // 반환
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return list;
	}

	// 장바구니 출력(하나씩) : 단품 or 세트 하나만 선태했을때
	public List<BasketDTO> OrderCartListTwo(String b_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BasketDTO> list = new ArrayList<BasketDTO>();
		try {
			conn = ds.getConnection();
			String sql = "select * from(\r\n"
					+ "select b.basket_code, b.b_id, b.product_code, b.s_name, b.amount, b.total_product_price, m.address, p.product_image,\r\n"
					+ "p.product_name, p.product_kind\r\n"
					+ "from basket b join product p on b.product_code = p.product_code\r\n"
					+ "              join member m on b_id = m_id \r\n" + "where b_id = ?\r\n"
					+ "order by basket_code desc)\r\n" + "where rownum between 1 and 2\r\n" + "order by basket_code asc"
					+ "";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, b_id);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				BasketDTO basketDto = new BasketDTO();
				basketDto.setBasket_code(rs.getInt("basket_code"));
				basketDto.setB_id(rs.getString("b_id"));
				basketDto.setProduct_code(rs.getInt("product_code"));
				basketDto.setS_name(rs.getString("s_name"));
				basketDto.setAmount(rs.getInt("amount"));
				basketDto.setTotal_product_price(rs.getInt("total_product_price"));
				basketDto.setAddress(rs.getString("address"));
				basketDto.setProduct_image(rs.getString("product_image"));
				basketDto.setProduct_name(rs.getString("product_name"));
				basketDto.setProduct_kind(rs.getString("product_kind"));
				list.add(basketDto);
			}

		} catch (Exception e) {
			System.out.println("OrderCartList: " + e.getMessage());
		} finally {
			try {
				DB_Close.close(pstmt);
				DB_Close.close(rs);
				DB_Close.close(conn); // 반환
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return list;
	}

	// 장바구니 하나씩 비우기
	public int OrderCartDelete(String b_id, int product_code) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		try {
			conn = ds.getConnection();
			String sql = "delete from basket where b_id=? and product_code=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, b_id);
			pstmt.setInt(2, product_code);
			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("OrderCartDelete 오류 :" + e.getMessage());
		} finally {
			try {
				DB_Close.close(pstmt);
				DB_Close.close(conn);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	// 장바구니 전체 비우기
	public int OrderCartTotalDelete(String b_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		try {
			conn = ds.getConnection();
			String sql = "delete from basket where b_id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, b_id);
			result = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("OrderCartDelete 오류 :" + e.getMessage());
		} finally {
			try {
				DB_Close.close(pstmt);
				DB_Close.close(conn);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
	

	// 주문관리에 사용
	public List<ProductDTO> selectProductByName(String product_name) {
		// 리넡할 객체 선언
		List<ProductDTO> productList = new ArrayList<ProductDTO>();
		Connection conn = null;
		product_name = product_name + "%";

		try {
			conn = ds.getConnection();
			String sql = "\r\n"
					+ "select p.product_code, p.product_name, p.product_price, p.product_kind, p.product_image, p.product_category, n.calorie\r\n"
					+ "from product p join nutrition n on p.nutrition_code = n.nutrition_code\r\n"
					+ "where product_name like ?" + "";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, product_name);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				ProductDTO productDto = new ProductDTO();
				productDto.setProduct_code(rs.getInt("product_code"));
				productDto.setProduct_name(rs.getString("product_name"));
				productDto.setProduct_price(rs.getInt("product_price"));
				productDto.setProduct_kind(rs.getString("product_kind"));
				productDto.setProduct_category(rs.getString("product_category"));
				productDto.setProduct_image(rs.getString("product_image"));
				productDto.setNutrition_code(rs.getInt("calorie"));
				// 칼로리 담을 dto 다시만들기 싫어서 임시로 Nutrition_code에 담음

				productList.add(productDto);

			}

		} catch (Exception e) {
			e.printStackTrace();
			productList = null;
		} finally {
			// 자원 반납
			DB_Close.close(rs);
			DB_Close.close(pstmt);
			DB_Close.close(conn);

		}
		return productList;
	}

	// 장바구니 추가
	public int OrderCartRegistger(BasketDTO basketDTO) {
		Connection conn = null;// 추가
		try {
			conn = ds.getConnection();
			String sql = "insert into basket (basket_code, b_id, product_code, s_name, amount, total_product_price) values(basket_sq.nextval,?,?,?,?,?)";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, basketDTO.getB_id());
			pstmt.setInt(2, basketDTO.getProduct_code());
			pstmt.setString(3, basketDTO.getS_name());
			pstmt.setInt(4, basketDTO.getAmount());
			pstmt.setInt(5, basketDTO.getTotal_product_price());

			result = pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Insert : " + e.getMessage());
		} finally {
			DB_Close.close(pstmt);
			try {
				DB_Close.close(conn); // 반환하기
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	// 자유게시판 글쓴이로 찾기
	public List<BoardFreeDTO> SearchFwriter(String fwriter) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BoardFreeDTO> list = new ArrayList<BoardFreeDTO>();

		try {
			conn = ds.getConnection();
			String sql = "select f_code, f_title, f_writer, f_date, f_readnum from board_free where f_writer like ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "%" + fwriter + "%");
			rs = pstmt.executeQuery();

			while (rs.next()) {
				BoardFreeDTO BoardFreeDto = new BoardFreeDTO();
				BoardFreeDto.setF_code(rs.getInt(1));
				BoardFreeDto.setF_title(rs.getString(2));
				BoardFreeDto.setF_writer(rs.getString(3));
				BoardFreeDto.setF_date(rs.getString(4));
				BoardFreeDto.setF_readnum(rs.getInt(5));
				list.add(BoardFreeDto);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				DB_Close.close(rs);
				DB_Close.close(pstmt);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;

	}

	// 자유게시판 글 제목으로 찾기
	public List<BoardFreeDTO> SearchFtitle(String ftitle) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BoardFreeDTO> list = new ArrayList<BoardFreeDTO>();
		System.out.println();
		try {
			conn = ds.getConnection();
			String sql = "select f_code, f_title, f_writer, f_date, f_readnum from board_free where f_title like ?";

			pstmt = conn.prepareStatement(sql);
			System.out.println("나오나요 타이틀!! " + ftitle);
			pstmt.setString(1, "%" + ftitle + "%");
			rs = pstmt.executeQuery();

			while (rs.next()) {
				BoardFreeDTO BoardFreeDto = new BoardFreeDTO();
				BoardFreeDto.setF_code(rs.getInt(1));
				BoardFreeDto.setF_title(rs.getString(2));
				BoardFreeDto.setF_writer(rs.getString(3));
				BoardFreeDto.setF_date(rs.getString(4));
				BoardFreeDto.setF_readnum(rs.getInt(5));
				list.add(BoardFreeDto);
			}
			System.out.println("--여긴 DAO" + list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				DB_Close.close(rs);
				DB_Close.close(pstmt);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;

	}

	// 댓글가져오기
	public List<ReplyDTO> selectCommentList(int f_code) {
		List<ReplyDTO> list = new ArrayList<ReplyDTO>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ds.getConnection();
			String sql = "select r_code, r_content, r_writer, r_write_date, f_code from reply where f_code=? order by r_code";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, f_code);
			rs = pstmt.executeQuery();

			list = new ArrayList<ReplyDTO>();
			while (rs.next()) {
				ReplyDTO ReplyDto = new ReplyDTO();
				ReplyDto.setR_code(rs.getInt("r_code"));
				ReplyDto.setR_content(rs.getString("r_content"));
				ReplyDto.setR_writer(rs.getString("r_writer"));
				ReplyDto.setR_write_date(rs.getString("r_write_date"));
				ReplyDto.setF_code(rs.getInt("f_code"));

				list.add(ReplyDto);
			}

		} catch (Exception e) {
			System.out.println("코멘트 리스트 에러 : " + e.getMessage());
		} finally {
			try {
				DB_Close.close(rs);
				DB_Close.close(pstmt);
				DB_Close.close(conn);

			} catch (Exception e2) {
				System.out.println("null 여기?" + e2.getMessage());
			}
		}
		return list;
	}

	// 댓글쓰기 함수
	public int insertComment(ReplyDTO Replydto) {
		int row = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = ds.getConnection();

			String sql = "insert into reply(r_code, r_writer, r_content, r_write_date, f_code)"
					+ "values(reply_sq.nextval, ?, ?, sysdate, ?)";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, Replydto.getR_writer());
			pstmt.setString(2, Replydto.getR_content());
			pstmt.setInt(3, Replydto.getF_code());

			row = pstmt.executeUpdate();
		} catch (Exception e1) {
			e1.getStackTrace();
			System.out.println("insert오류: " + e1.getMessage());
		} finally {
			try {
				if (pstmt != null)
					try {
						DB_Close.close(pstmt);
					} catch (Exception e2) {
					}
				if (conn != null)
					try {
						DB_Close.close(conn);
					} catch (Exception e3) {
					}
			} catch (Exception e4) {
				e4.getStackTrace();
				DB_Close.close(rs);

			}

		}
		return row;
	}

	// 댓글 삭제하기
	public int deleteComment(ReplyDTO Replydto) {
		int row = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = ds.getConnection();
			String sql = "delete from reply where f_code=? and r_code=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, Replydto.getF_code());
			pstmt.setInt(2, Replydto.getR_code());

			row = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("delete : " + e.getMessage());
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (Exception e2) {
				System.out.println("delete : " + e2.getMessage());
			}
		}
		return row;
	}

	// 댓글 수정하기
	public int updateComment(ReplyDTO Replydto) {
		int row = 0;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = ds.getConnection();
			String sql = "update reply set r_content=? where f_code=? and r_code=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, Replydto.getR_content());
			pstmt.setInt(2, Replydto.getF_code());
			pstmt.setInt(3, Replydto.getR_code());
			row = pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("수정dao 에러: " + e.getMessage());
			e.getStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();// 반환
			} catch (Exception e2) {
				System.out.println(e2.getMessage());
			}
		}
		return row;
	}
	
	   //자유게시판 댓글 개수 가져오기
	   public int totalReplyCount(int f_code) {
	      Connection conn = null;
	      PreparedStatement pstmt = null; //
	      ResultSet rs = null;
	      int totalreplycount = 0;
	      try {
	         conn = ds.getConnection();
	         String sql = "select count(r_code) rcode from reply where f_code = ?"; //
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setInt(1, f_code);
	         rs = pstmt.executeQuery();
	         if (rs.next()) {
	            totalreplycount = rs.getInt(1); 
	         }
	      } catch (Exception e) {
	         System.out.println(e.getMessage());
	      } finally {
	         try {
	            pstmt.close();
	            rs.close();
	            conn.close(); 
	         } catch (Exception e2) {
	            System.out.println(e2.getMessage());
	         }
	      }
	      return totalreplycount;
	   }
	   

	// 장바구니 - 상세보기(상품) : 상품번호를 이용해 가격 얻기
	public ProductDTO PrductDetail(int product_code) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ProductDTO productDto = new ProductDTO();
		try {
			conn = ds.getConnection();
			String sql = "select * from product where product_code = ?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, product_code);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				productDto.setProduct_code(rs.getInt("product_code"));
				productDto.setProduct_category(rs.getString("product_category"));
				productDto.setProduct_name(rs.getString("product_name"));
				productDto.setProduct_price(rs.getInt("product_price"));
				productDto.setProduct_kind(rs.getString("product_kind"));
				productDto.setProduct_image(rs.getString("product_image"));
			}

		} catch (Exception e) {
			System.out.println(" PrductDetail : " + e.getMessage());
		} finally {
			try {
				DB_Close.close(pstmt);
				DB_Close.close(rs);
				DB_Close.close(conn); // 반환
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return productDto;
	}

	// 주문 테이블 삽입
	public int OrdersTableInsert(OrdersDTO ordersDTO) {
		Connection conn = null;// 추가
		try {
			conn = ds.getConnection();
			String sql = "insert into orders(order_code,o_id,s_name,payment_method,payment_price,payment_date) values(orders_sq.nextval,?,?,?,?,sysdate)";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, ordersDTO.getO_id());
			pstmt.setString(2, ordersDTO.getS_name());
			pstmt.setString(3, ordersDTO.getPayment_method());
			pstmt.setInt(4, ordersDTO.getPayment_price());

			result = pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Insert : " + e.getMessage());
		} finally {
			DB_Close.close(pstmt);
			try {
				DB_Close.close(conn); // 반환하기
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	// 주문 테이블 주문번호 최대값 추출
	public int getMaxOrderCode() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int orderCode = 0;
		try {
			conn = ds.getConnection(); //
			String sql = "select nvl(max(order_code),0) from orders";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				orderCode = rs.getInt(1);
			}
		} catch (Exception e) {
			System.out.println("getMaxOrderCode : " + e.getMessage());
		} finally {
			try {
				pstmt.close();
				rs.close();
				conn.close(); // �
			} catch (Exception e) {

			}
		}
		return orderCode;

	}

	// 장바구니에 있는 데이터만 추출(주문상세에 넣기)
	public List<BasketDTO> selectBasket(String b_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BasketDTO> list = new ArrayList();
		int orderCode = 0;
		try {
			conn = ds.getConnection(); //
			String sql = "select * from basket where b_id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, b_id);
			rs = pstmt.executeQuery();

			rs = pstmt.executeQuery();
			while (rs.next()) {
				BasketDTO basketDto = new BasketDTO();
				basketDto.setProduct_code(rs.getInt("product_code"));
				basketDto.setAmount(rs.getInt("amount"));
				list.add(basketDto);
			}
		} catch (Exception e) {
			System.out.println("selectBasket : " + e.getMessage());
		} finally {
			try {
				pstmt.close();
				rs.close();
				conn.close(); // �
			} catch (Exception e) {

			}
		}
		return list;

	}

	// 주문상세 테이블에 삽입
	public int OrdersDetailTableInsert(int product_code, int order_code, int order_amount) {
		Connection conn = null;// 추가
		try {
			conn = ds.getConnection();
			String sql = "insert into order_detail(product_code, order_code, order_amount) values(?,?,?)";

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, product_code);
			pstmt.setInt(2, order_code);
			pstmt.setInt(3, order_amount);

			result = pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("OrdersDetailTableInsert : " + e.getMessage());
		} finally {
			DB_Close.close(pstmt);
			try {
				DB_Close.close(conn); // 반환하기
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

}
