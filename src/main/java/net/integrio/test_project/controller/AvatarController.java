package net.integrio.test_project.controller;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.core.env.Environment;
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
@Log
public class AvatarController {

    private Environment environment;
    private ServletContext context;
    private ServletConfig config;

    @RequestMapping("users/endpoint")
    public void start(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String folder = environment.getProperty("images.location");

        String avatarpath = (new File(
                folder + "avatar_" + session.getAttribute("username") + ".jpg").exists())
                ? folder + "avatar_" + session.getAttribute("username") + ".jpg"
                : folder + "/noavatar.jpg";

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
            if (output != null) try {
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
    }
}