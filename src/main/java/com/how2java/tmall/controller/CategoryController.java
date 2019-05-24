//package com.how2java.tmall.controller;
//
//import com.how2java.tmall.pojo.Category;
//import com.how2java.tmall.service.CategoryService;
//import org.springframework.beans.factory.annotation.Autowired;;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.List;
//
///**
// */
//@Controller
//@RequestMapping("")
//public class CategoryController {
//    @Autowired
//    CategoryService categoryService;
//    @RequestMapping("/admin_category_list")
//    public String list(Model model){
//        List<Category> categories = categoryService.list();
//        model.addAttribute("cs",categories);
//        return "admin/listCategory";
//    }
//}
package com.how2java.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.util.Page;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.util.ImageUtil;
import com.how2java.tmall.util.UploadImageFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    /**
     * 查询所有
     * @param model
     * @return
     @RequestMapping("admin_category_listAll") public String listAll(Model model){
     List<Category> categories = categoryService.list();
     model.addAttribute("cs", categories);
     return "admin/listCategory";
     }
     */
    /**
     * 查询所有，带分页功能
     *
     * @return
     * @RequestMapping("admin_category_list") public String list(Model model, Page page){
     * int total = categoryService.total();
     * page.setTotal(total);
     * List<Category> categories = categoryService.list(page);
     * model.addAttribute("cs",categories);
     * model.addAttribute("page",page);
     * return "admin/listCategory";
     * }
     */
    @RequestMapping("admin_category_list")
    public String list(Model model, Page page) {
        //1.通过分页插件指定分页参数
        PageHelper.offsetPage(page.getStart(), page.getCount());
        //2.调用list()获取对应分页的数据
        List<Category> categories = categoryService.list();
        //3.获取总的记录数
        int total = (int) new PageInfo<>(categories).getTotal();
        page.setTotal(total);
        model.addAttribute("cs", categories);
        model.addAttribute("page", page);
        return "admin/listCategory";
    }

    @RequestMapping("admin_category_add")
    public String add(Category c, HttpSession session, UploadImageFile uploadImageFile) throws IOException {
        //1.向数据库添加数据
        categoryService.add(c);
        //2.确定图片存放位置
        File imageFolder = new File(session.getServletContext().getRealPath("img/category"));
        //3.根据分类id创建文件
        File file = new File(imageFolder, c.getId() + ".jpg");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        uploadImageFile.getImage().transferTo(file);
        BufferedImage img = ImageUtil.change2jpg(file);
        ImageIO.write(img, "jpg", file);
        return "redirect:/admin_category_list";
    }

    @RequestMapping("admin_category_delete")
    public String delete(int id, HttpSession session) {
        //1.从数据库删除category文本信息
        categoryService.delete(id);
        //2.从图库删除图片信息
        File imageFolder = new File(session.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, id + ".jpg");
        if (file.exists())
            file.delete();
        return "redirect:/admin_category_list";
    }

    @RequestMapping("admin_category_edit")
    public String edit(int id, Model model) {
        Category category = categoryService.get(id);
        model.addAttribute("c", category);
        return "admin/editCategory";
    }

    @RequestMapping("admin_category_update")
    public String update(Category c, HttpSession session, UploadImageFile uploadImageFile) throws IOException {
        //1.更新数据库信息
        categoryService.update(c);
        //2.更新图片信息
        MultipartFile image = uploadImageFile.getImage();
        if (image != null && !image.isEmpty()) {
            //上传信息含有图片,则保存图片
            File imageFolder = new File(session.getServletContext().getRealPath("img/category"));
            File file = new File(imageFolder, c.getId() + ".jpg");
            image.transferTo(file);
            BufferedImage img = ImageUtil.change2jpg(file);
            ImageIO.write(img, "jpg", file);
        }
        return "redirect:/admin_category_list";
    }
}