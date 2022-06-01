package net.integrio.test_project.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;

@Controller
@AllArgsConstructor
public class AvatarController {

    private ServletContext context;
    private ServletConfig config;

    @RequestMapping("users/endpoint")
    public String start(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String avatarpath = (new File(
                "C:/Users/Acer/IdeaProjects/filter_proj/web/img/" + session.getAttribute("username") + "_avatar.jpg").exists())
                ? "C:/Users/Acer/IdeaProjects/filter_proj/web/img/" + session.getAttribute("username") + "_avatar.jpg"
                : "C:/Users/Acer/IdeaProjects/filter_proj/web/img/noavatar.jpg";
        File file = new File(avatarpath);
        context = config.getServletContext();
        response.setContentType(context.getMimeType(file.getName()));
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");

        BufferedOutputStream output = null;
        BufferedInputStream input = null;
        try {
            input = new BufferedInputStream(new FileInputStream(file));
            output = new BufferedOutputStream(response.getOutputStream());

            byte[] buffer = new byte[10240];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) try {//??getOutputStream() has already been called for this response
                output.close();
            } catch (IOException exception) {
                System.out.println("Exception" + exception);
            }
            if (input != null) try {
                input.close();
            } catch (IOException exception) {
                System.out.println("Exception" + exception);
            }
        }
        return "users/endpoint";
    }
}