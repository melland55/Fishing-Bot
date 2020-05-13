import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
 
import javax.imageio.ImageIO;

class Fisher{
    public static int[] getBobberLocation(BufferedImage image){
        int[] bobberLocation = new int[2];
        for(int i = 0; i < 1280; i += 4){
            for(int j = 0; j < 720; j+= 4){
                int pixel = image.getRGB(i, j);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                if(red >= 60 && green <= 30 && blue <= 30){
                    bobberLocation[0] = i + 1280;
                    bobberLocation[1] = j + 720;
                    return bobberLocation;
                }
            }
        }
        return bobberLocation;
    }

    public static void catchFish(int[] bobber){
        int countSec = 0;
        int prevGreyValue = 0;
        while(true){
            try {
                Robot robot = new Robot();
                Rectangle bobberRect = new Rectangle(bobber[0] - 10, bobber[1] - 10, 20, 20);
                BufferedImage bobberImage = robot.createScreenCapture(bobberRect);
                //ImageIO.write(bobberImage, "jpg", new File("bobber.jpg"));
                int blue = 0;
                int red = 0;
                int green = 0;
                for(int i = 0; i < 20; i++){
                    for(int j = 0; j < 20; j++){
                        int pixel = bobberImage.getRGB(i, j);
                        red += (pixel >> 16) & 0xff;
                        green += (pixel >> 8) & 0xff;
                        blue += (pixel) & 0xff;
                    }
                }
                blue /= 400;
                red /= 400;
                green /= 400;
                int grey = (red + blue + green) / 3;
                if(Math.abs(prevGreyValue - grey) > 7 && prevGreyValue != 0){        
                    return;
                }
                System.out.println(grey + " : " + prevGreyValue + " : " + (prevGreyValue - grey));
                prevGreyValue = grey;
            } catch (AWTException ex) {
                System.err.println(ex);
            }
            try {
                Thread.sleep(100);
             } catch (Exception e) {
                System.out.println(e);
             }
             countSec++;
             System.out.println("Time Elapsed: " + (countSec / 10) + " seconds");
             
        }
    }
    public static void main(String[] args){
        try {
            Robot robot = new Robot();
            String format = "jpg";
            String fileName = "FullScreenshot." + format;
            Rectangle screenRect = new Rectangle(1280, 720, 1280, 720);
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            ImageIO.write(screenFullImage, format, new File(fileName));
            int[] bobberLocation = new int[2];
            bobberLocation = getBobberLocation(screenFullImage);
            System.out.print("Found Lure at " + bobberLocation[0] + ", " + bobberLocation[1]);
            catchFish(bobberLocation);
            robot.mouseMove(bobberLocation[0], bobberLocation[1]);
            try{
                Thread.sleep(100);
            }catch (Exception e) {
                System.out.println(e);
            }
            robot.mousePress(InputEvent.BUTTON1_MASK);
            try{
                Thread.sleep(100);
            }catch (Exception e) {
                System.out.println(e);
            }
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            try{
                Thread.sleep(400);
            }catch (Exception e) {
                System.out.println(e);
            }
            robot.mousePress(InputEvent.BUTTON3_MASK);
            try{
                Thread.sleep(100);
            }catch (Exception e) {
                System.out.println(e);
            }
            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
        } catch (AWTException | IOException ex) {
            System.err.println(ex);
        }
    }
}