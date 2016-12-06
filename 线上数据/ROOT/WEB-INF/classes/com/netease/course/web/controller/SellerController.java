package com.netease.course.web.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.netease.course.dao.ContentMapper;
import com.netease.course.entity.Content;
import com.netease.course.entity.Person;

@Controller
public class SellerController {

	@Autowired
	private ContentMapper contentMapper;

	@RequestMapping("/public")
	public String publics(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		System.out.println("/public");
		HttpSession session = request.getSession();
		Person person = (Person) session.getAttribute("user");
		if (person != null && person.getUsertype() == 1) {
			return "public";
		} else {
			response.sendRedirect("/");
			return null;
		}
	}

	@RequestMapping("/publicSubmit")
	public String publish(HttpServletRequest request,
			HttpServletResponse response, @RequestParam("title") String title,
			@RequestParam("summary") String summary,
			@RequestParam("image") String image,
			@RequestParam("detail") String detail,
			@RequestParam("price") String price, ModelMap map)
			throws IOException {
		System.out.println("/publicSubmit");
		System.out.println("title = " + title);
		HttpSession session = request.getSession();
		Person person = (Person) session.getAttribute("user");
		if (person != null && person.getUsertype() == 1) {
			Content product = new Content();
			price = smalltobig(price);
			System.out.println("----price= " + price);
			Long Price = Long.valueOf(price);
			if(Price<=0){
				map.addAttribute("product", null);
				return "publicSubmit";
			}
			product.setTitle(title);
			product.setSummary(summary);
			product.setImage(image);
			product.setDetail(detail);
			product.setPrice(Price);
			
			if (contentMapper.selectCount(product) > 1000) {
				response.sendRedirect("/");
			}
			;
			contentMapper.insert(product);
			System.out.println(product.getId());
			map.addAttribute("product", product);

			return "publicSubmit";
		} else {
			response.sendRedirect("/");
			return null;
		}
	}

	@RequestMapping(value = "/api/delete", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap deleteItem(@RequestParam("id") String ItemId,
			HttpServletRequest request, HttpServletResponse response) {
		System.out.println("/api/delete");
		HttpSession session = request.getSession();
		Person person = (Person) session.getAttribute("user");
		if (person != null && person.getUsertype() == 1) {
			// System.out.println(ItemId);
			contentMapper.deleteByPrimaryKey(Integer.valueOf(ItemId));
			ModelMap map = new ModelMap();
			map.addAttribute("code", "200");
			map.addAttribute("message", "success");
			map.addAttribute("result", "true");
			return map;
		} else {
			ModelMap map = new ModelMap();
			map.addAttribute("code", "500");
			map.addAttribute("message", "没有权限，请登录。");
			map.addAttribute("result", "false");
			return map;
		}

	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String editItem(@RequestParam("id") String ItemId, ModelMap map,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("/edit");
		System.out.println(ItemId);
		HttpSession session = request.getSession();
		Person person = (Person) session.getAttribute("user");
		if (person != null && person.getUsertype() == 1) {
			Content product = contentMapper.selectByPrimaryKey(Integer
					.valueOf(ItemId));
			// ModelMap map = new ModelMap();
			map.addAttribute("product", product);
			// System.out.println(product);

			// System.out.println("map = =====" + map);

			return "edit";

		} else {
			response.sendRedirect("/");
			return null;
		}
	}

	@RequestMapping(value = "/editSubmit", method = RequestMethod.POST)
	public String editSubmit(@RequestParam("id") String ItemId,
			@RequestParam("title") String title,
			@RequestParam("summary") String summary,
			@RequestParam("image") String image,
			@RequestParam("detail") String detail,
			@RequestParam("price") String price, ModelMap map,
			HttpServletRequest request, HttpServletResponse response) {
		System.out.println("/editSubmit");
		System.out.println("title = " + title);
		// System.out.println(map);
		// "id,id"
		
		HttpSession session = request.getSession();
		Person person = (Person) session.getAttribute("user");
		if(person!=null&&person.getUsertype()==1){
		StringBuffer stb = new StringBuffer(ItemId.substring(ItemId
				.indexOf(",") + 1));
		int itemid = Integer.valueOf(stb.toString());
		// System.out.println(ItemId.substring(ItemId.indexOf(",")+1));
		Content product = contentMapper.selectByPrimaryKey(itemid);
		// 回调
		System.out.println("/get product");
		price = smalltobig(price);
					Long Price = Long.valueOf(price);
			if(Price<=0){
				map.addAttribute("product", null);
				return "publicSubmit";
			}

		product.setTitle(title);
		product.setSummary(summary);
		product.setImage(image);
		product.setDetail(detail);
		product.setPrice(Price);

		contentMapper.updateByPrimaryKey(product);
		// ModelMap map = new ModelMap();
		map.addAttribute("product", product);
		// System.out.println(map);
		// System.out.println("to Update product");
		// System.out.println(((Content)map.get("product")).getTitle());
		return "editSubmit";}
		else {
			try {
				response.sendRedirect("/");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return "/";
			}
			return null;
		}
		
	}
	
	@RequestMapping(value = "/api/upload", method = RequestMethod.POST)
	public ModelMap uploadPicture(ModelMap map,@RequestParam("file") MultipartFile file,HttpServletRequest request, HttpServletResponse response){
		String rootpath = request.getSession().getServletContext().getRealPath("/");
		String filesavePre = md5(Long.valueOf(System.currentTimeMillis()).toString()); 
		File destfile = new File(rootpath+"/image/__"+filesavePre+".jpg");
		String filename = file.getOriginalFilename();
		String url = new String("/image/")+destfile.getName();
		//request.getContextPath()+
		if(filename.endsWith(".jpg")||filename.endsWith(".png")
				||filename.endsWith(".bmp")||filename.endsWith(".gif")
				||filename.endsWith(".jpeg")){
			
			//File test = new File("ying");
			//System.out.println(test.getAbsolutePath());
			//file.getOriginalFilename()
			
			HttpSession session = request.getSession();
			Person person = (Person) session.getAttribute("user");
			if(person!=null&&person.getUsertype()==1){
				
			}else{
				return null;
			}
			try {
				System.out.println("==save File Begin");
				file.transferTo(destfile);
				System.out.println("==save File Doing");
				System.out.println("===File at :"+destfile.getAbsolutePath());
				
			} catch (IllegalStateException e  ) {
				// TODO Auto-generated catch block
				System.out.println("error IllegalState");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("error IO exception");
				//e1.printStackTrace();
			}
			
			
			map.addAttribute("code", "200");
			map.addAttribute("message", "success");
			map.addAttribute("result", url);
			return map;		
		}
		response.setStatus(400);
		map.addAttribute("code", 400);
		map.addAttribute("message", "格式非法");
		map.addAttribute("result", false);
		return map;
		
	}
	//存入数据全部*100
	/**
	 * 
	 * @param 用户的价格price
	 * @return 要存储的价格,只精确2位
	 */
	public String smalltobig(String price){
		Double realprice = Double.valueOf(price);
		Double saveprice = realprice*100;
		
		Long number = Math.round(saveprice.doubleValue());
		return number.toString();
	}
	
	//md5加密算法,单向加密，没有解密的
    
    public static String md5(String str){
        byte[] bytes = null;
        try {
            bytes = MessageDigest.getInstance("md5").digest(str.getBytes());//得到MD5的实例，再将字符串加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new BigInteger(1,bytes).toString(16);//把字节数组转化为正的bigInteger,然后变成16进制表示
    }

}
