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
//testing commit



class Fisher{


    public static void sleep(int miliseconds){
        try{
            Thread.sleep(miliseconds);
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public static BufferedImage getImage(Rectangle rectangle){
        try{
            Robot robot = new Robot();
            BufferedImage bobberImage = robot.createScreenCapture(rectangle);
            return bobberImage;
        }catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static void tabOut(){
        try{
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ALT);
            sleep(100);
            robot.keyPress(KeyEvent.VK_TAB);
            sleep(200);
            robot.keyRelease(KeyEvent.VK_ALT);
            robot.keyRelease(KeyEvent.VK_TAB);
        }catch (Exception e) {
            System.out.println(e);
        }
    }

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
                Rectangle bobberRect = new Rectangle(bobber[0], bobber[1], 20, 20);
                BufferedImage bobberImage = robot.createScreenCapture(bobberRect);
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
                //System.out.println(grey + " : " + prevGreyValue + " : " + (prevGreyValue - grey));
                prevGreyValue = grey;
            } catch (AWTException ex) {
                System.err.println(ex);
            }
            sleep(100);
             countSec++;
             if(countSec % 10 == 0){
                System.out.println("Time Elapsed: " + (countSec / 10) + " seconds");
             }
             if(countSec / 10 > 20){
                 return;
             }
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
            sleep(1000);
            robot.mousePress(InputEvent.BUTTON1_MASK);
            sleep(100);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            sleep(400);
            robot.mousePress(InputEvent.BUTTON3_MASK);
            sleep(100);
            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            sleep(2000);
            tabOut();
        } catch (AWTException | IOException ex) {
            System.err.println(ex);
        }
    }
}