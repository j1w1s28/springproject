package com.wewishwell.shop.controller;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.wewishwell.shop.service.MainService;
import com.wewishwell.shop.service.MemberService;
import com.wewishwell.shop.service.ReviewService;
import com.wewishwell.shop.vo.BasketVO;
import com.wewishwell.shop.vo.MemberVO;
import com.wewishwell.shop.vo.ProdLikeVO;
import com.wewishwell.shop.vo.ProductVO;
import com.wewishwell.shop.vo.ReviewVO;
import com.wewishwell.shop.vo.ReviewlikeVO;

@Controller
public class MainController {

	@Autowired
	MainService ms;
	
	@Autowired
	MemberService mbs;
	
	@Autowired
	ReviewService reviewservice;
	
	@GetMapping("test")
	public String testing() {
		return "test";
	}

	@GetMapping("/")
	public String index(HttpServletRequest req) {
		Cookie cookie = WebUtils.getCookie(req, "nm_ID");
		HttpSession s = req.getSession();
		String data = (String)s.getAttribute("data");
		if(data == null) {
			if(cookie !=null) {
				
				s.setAttribute("data", cookie.getValue());
			}else {
				String Cookie_id = String.valueOf(Math.random()); 
				Cookie cookie2 = new Cookie("nm_ID", Cookie_id);
				cookie2.setMaxAge(60*60*24*1);
				s.setAttribute("data", cookie2.getValue());
			}
		}
		return "index";
	}

    // === 마이 페이지
	@GetMapping("/mypage")
	public ModelAndView mypage() {
		// service

		ModelAndView mav = new ModelAndView();
		mav.setViewName("mypage"); // list.jsp

		return mav;
	}
	
    // === 제품페이지 카테고리
	@GetMapping("product")
	public ModelAndView productlist(@RequestParam Map<String, String> map) {
		List<ProductVO> productList = new ArrayList<ProductVO>();
		ModelAndView mav = new ModelAndView();
		productList = ms.productList(map);
		mav.addObject("data", productList);
		mav.addObject("category",map.get("category"));
		mav.setViewName("productView"); // list.jsp
		
		return mav;
	}

    // === 검색
	@PostMapping("product")
	public ModelAndView getSearchList(@RequestParam Map<String,String> map) {
		ModelAndView mav = new ModelAndView();
		List<ProductVO> searchList = ms.getSearchList(map);
		mav.addObject("data", searchList);
		mav.setViewName("productView");
		return mav;
	}

    // === 제품 상세 페이지
	@GetMapping("/productDetail")
	public ModelAndView productDetail(ProductVO productVO, HttpServletRequest req) {
		// service
		ModelAndView mav = new ModelAndView();
		ProductVO productDetail = ms.productDetail(productVO);
		HttpSession s = req.getSession();
		String id = (String)s.getAttribute("data");
		if(id == null) {
			id = "";
		}
		List<ReviewlikeVO> checkreviewnum = reviewservice.checkreviewnum(id);
		if(checkreviewnum != null) {
			mav.addObject("checkreviewnum", checkreviewnum);
			
		}
		ProdLikeVO plVO = new ProdLikeVO(productVO.getId(), id);
		int check = ms.likeCheck(plVO);
		List<ReviewVO> reviewlist = new ArrayList<ReviewVO>();
		reviewlist = reviewservice.getreviewlist(productVO);
		List<ReviewVO> reviewlist_by_star = reviewservice.reviewlist_sort_by_star(productVO);
		int cnt5star = reviewservice.cnt5star(productVO);
		int cnt4star = reviewservice.cnt4star(productVO);
		int cnt3star = reviewservice.cnt3star(productVO);
		int cnt2star = reviewservice.cnt2star(productVO);
		int cnt1star = reviewservice.cnt1star(productVO);
		int cntreview = reviewservice.cntreview(productVO);	
		Double avgreview = reviewservice.avgreview(productVO);
		mav.addObject("likeCheck", check);
		mav.addObject("reviewlist_by_star", reviewlist_by_star);
		mav.addObject("data", productDetail); // request.setAttribute("data",list)
		mav.addObject("review",reviewlist);
		mav.addObject("cnt5star",cnt5star);
		mav.addObject("cnt4star",cnt4star);
		mav.addObject("cnt3star",cnt3star);
		mav.addObject("cnt2star",cnt2star);
		mav.addObject("cnt1star",cnt1star);
		mav.addObject("cntreview",cntreview);
		mav.addObject("avgreview", avgreview);
		mav.setViewName("productDetail"); // list.jsp
		
		return mav;
	}

		// ===  제품 상세페이지에서 바로 구매
		@GetMapping("purchaseOne")
		public ModelAndView purchaseOne(@RequestParam Map<String, String> map) {
			
			boolean isNumeric =  map.get("user_id").matches("[+-]?\\d*(\\.\\d+)?"); // 아이디가 숫자인지 체크

			// 회원일 경우 실행할 코드
			if(!isNumeric) {
				MemberVO vo = new MemberVO();
				vo.setId(map.get("user_id"));
				MemberVO vo2 = mbs.memberdetail(vo);
				map.put("user_name", vo2.getName());
				map.put("address", vo2.getAddress());
			}
			
			// 비회원일 경우 user_id를 nonmember로 치환
//			else {
//				map.put("user_id", "nonmember");
//			}
			
			String a = Integer.toString(Integer.parseInt(map.get("p_p"))*Integer.parseInt(map.get("p_c")));
			String ttl_cnt = Integer.toString((Integer.parseInt(map.get("p_c"))));
			map.put("amount", a);
			map.put("ttl_cnt", ttl_cnt);
			map.put("method", "제품바로구매");
			
			ModelAndView mav = new ModelAndView();
			mav.addObject("data", map);
			mav.setViewName("purchase");

			return mav;
		}
		
		// === 장바구니 담기
		@GetMapping("/basket")
		public String basket(BasketVO basketVo) {
			
			String modal = "";
			
			// member id, prod id - 이전에 담아둔 상품이 존재하는지 확인
			BasketVO check = ms.checkBasket(basketVo);
			
			if(check == null) {
				int rs = ms.addBasket(basketVo);
				
				if(rs == 1) {
					modal = "addBasketModal";
				}
			} else {
				int rs = ms.updateBasket(basketVo);
				
				if(rs == 1) {
					modal = "addBasketModal";
				}
			}
			return modal;
		}

	// === 장바구니 목록 페이지
		@GetMapping("/basketList")
		public ModelAndView basketList(MemberVO MemberVO) {

			// service
			List<Map<String, Object>> basketList = ms.basketList(MemberVO);

			ModelAndView mav = new ModelAndView();
			mav.addObject("data", basketList);
			mav.setViewName("basket");

			return mav;
		}
	
	// === 장바구니 제품 삭제
		@GetMapping("/basketDelete")
		public String basketDelete(BasketVO basketVo, HttpServletRequest req) {

			// service
			int rs = ms.deleteBasket(basketVo);

			if(rs == 1) {
				return "redirect:" + req.getHeader("Referer");
			} else {
				return "index";
			}

		}
	
	// === 장바구니에서 구매
		@GetMapping("/purchase")
		public ModelAndView purchase(MemberVO MemberVO) {
			
			Map<String, Object> orderInfo = ms.orderInfo(MemberVO);
			
			orderInfo.put("method", "장바구니구매");
			
			// service
			ModelAndView mav = new ModelAndView();
			mav.addObject("data", orderInfo);
			mav.setViewName("purchase");

			return mav;
		}
		
	// ===  구매페이지 : 구매 완료, order테이블 추가
		@PostMapping("/purchase")
		public ModelAndView purchase_post(@RequestParam Map<String, Object> order) {
			ModelAndView mav = new ModelAndView();
			order.put("receiver_address", order.get("address"));
			order.remove("addr");
			order.remove("address");
			// 비회원일경우 
//			System.out.println(order);
//			boolean isNumeric =  ((String)order.get("user_id")).matches("[+-]?\\d*(\\.\\d+)?"); // 아이디가 숫자인지 체크
//			if(isNumeric) {
//				order.put("user_id", "nonmember");
//			}
			
			// === order 테이블 추가 ===
			ms.insertOrder(order);
			int orderNum = ms.selectOrderNum(order); // 주문번호 
			order.put("order_num", orderNum);
			
			if(order.get("buy_method").equals("장바구니구매")) {
				
				// === order_detail 테이블 추가 ===
				int rs = ms.insertOrderDetail(order);
				
				if(rs > 0) {
					// === 장바구니 비우기 ===
					BasketVO basketVo = new BasketVO();
					basketVo.setUser_id((String)order.get("user_id"));
					ms.deleteBasket(basketVo);
					
					// === 주문내역 불러오기
					mav.setViewName("redirect:/orderTracking?order_num="+orderNum+"&order_Name="+(String)order.get("receiver_name")); 
				}
			}
			else if(order.get("buy_method").equals("제품바로구매")) {
				
				// === order_detail 테이블 추가 ===
				ms.insertOrderDetailOne(order);
				
				mav.setViewName("redirect:/orderTracking?order_num="+orderNum+"&order_Name="+(String)order.get("receiver_name")); 
			}
			return mav;
		}

	    // === 주문확인서 페이지
		@GetMapping("/orderTracking")
		public ModelAndView orderTracking(@RequestParam Map<String, Object> order) {
			
			List<Map<String, Object>> orderlist = ms.getOrderList(order);
			
			ModelAndView mav = new ModelAndView();
			mav.addObject("data", orderlist);
			mav.setViewName("orderTracking");

			return mav;
		}

	// === 찜 추가
	@GetMapping("likeList")
	public ModelAndView likeList(@RequestParam Map<String, String> map) {
		List<Map<String, Object>> likeList = ms.likeList(map);
		ModelAndView mav = new ModelAndView();
		mav.addObject("data",likeList);
		mav.setViewName("like");
		return mav;
		
	}
	
    // === 찜 삭제
	@GetMapping("deleteLike")
	public String deleteLike(ProdLikeVO vo, HttpServletRequest req) {
		int check = ms.deleteLike(vo);
		if(check == 1) {
			return "redirect:" + req.getHeader("Referer");
		}
		return "index";
	}
	
    // === 구매내역 페이지
	@GetMapping("buyList")
	public ModelAndView buyList(String id) {
		ModelAndView mav = new ModelAndView();
		List<Map<String, String>> map = ms.buyList(id);
		List<Integer> map2 = mbs.findReview(id);
		mav.addObject("data", map);
		mav.addObject("check", map2);
		mav.setViewName("buyList");
		return mav;
	}
	
	@GetMapping("delReview")
	public String delReview(String odm, String id) {
		int check = ms.delReview(odm);
		if(check == 1) {
			return "redirect:/buyList?id="+id;
		} else {
			return "index";
		}
	}
	
	// === 비회원 구매내역 페이지
	@GetMapping("buyListNonmem")
	public ModelAndView buyListNonmem() {
		
		// service
		ModelAndView mav = new ModelAndView();
		mav.setViewName("buyListNonmem");
		
		return mav;
	}

}
