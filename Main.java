import java.net.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.*;
import javafx.scene.paint.*;
import javafx.geometry.*;
import javafx.scene.image.*;

import java.io.*;

import java.util.*;
import java.text.*;
import java.io.*;
import java.lang.*;
import javafx.application.*;
import javafx.event.*;
import javafx.stage.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.animation.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import java.net.*;
import javafx.geometry.*;
import java.io.FileOutputStream;
import java.io.PrintWriter;


public class Main extends Application
{
   FlowPane fp;
   AnimationHandler ta;
   Canvas theCanvas = new Canvas(600,600);
   TestObject thePlayer = new TestObject(300,300);
   TestObject origin = new TestObject(300,300);
   ArrayList<Mine> mines = new ArrayList<Mine>();
   ArrayList<Integer> cposx = new ArrayList<Integer>();
   ArrayList<Integer> cposy = new ArrayList<Integer>();
   GraphicsContext gc;
   int highscore = 0;
   int cgridx, cgridy;
   Random rand = new Random();

   public void start(Stage stage)
   {
      fp = new FlowPane();
      fp.getChildren().add(theCanvas);
      gc = theCanvas.getGraphicsContext2D();
      drawBackground(300,300,gc);
      
      //draws the player and initializes the origin
      thePlayer.draw(300,300,gc,true);
      origin.draw(300,300,gc,false);
      
      //adds action to keys
      fp.setOnKeyPressed(new KeyListenerDown());
      fp.setOnKeyReleased(new KeyListenerUp());
      
      //add aninmation to player
      ta = new AnimationHandler();
      ta.start();
      
      //set the scene
      Scene scene = new Scene(fp, 600, 600);
      stage.setScene(scene);
      stage.setTitle("Project :)");
      stage.show();
      
      fp.requestFocus();
   }
   
   
   
   //creating the background
   Image background = new Image("stars.png");
   Image overlay = new Image("starsoverlay.png");
   Random backgroundRand = new Random();
   //this piece of code doesn't need to be modified
   public void drawBackground(float playerx, float playery, GraphicsContext gc)
   {
	  //re-scale player position to make the background move slower. 
      playerx*=.1;
      playery*=.1;
   
	//figuring out the tile's position.
      float x = (playerx) / 400;
      float y = (playery) / 400;
      
      int xi = (int) x;
      int yi = (int) y;
      
	  //draw a certain amount of the tiled images
      for(int i=xi-3;i<xi+3;i++)
      {
         for(int j=yi-3;j<yi+3;j++)
         {
            gc.drawImage(background,-playerx+i*400,-playery+j*400);
         }
      }
      
	  //below repeats with an overlay image
      playerx*=2f;
      playery*=2f;
   
      x = (playerx) / 400;
      y = (playery) / 400;
      
      xi = (int) x;
      yi = (int) y;
      
      for(int i=xi-3;i<xi+3;i++)
      {
         for(int j=yi-3;j<yi+3;j++)
         {
            gc.drawImage(overlay,-playerx+i*400,-playery+j*400);
         }
      }
   }
   
   boolean up,down,left,right; //link to the keys
   boolean lose = false;
   boolean newHighScore = false;
   public class AnimationHandler extends AnimationTimer
   {
      public void handle(long currentTimeInNanoSeconds) 
      {  
         gc = theCanvas.getGraphicsContext2D();
         gc.clearRect(0,0,600,600);
         
         //USE THIS CALL ONCE YOU HAVE A PLAYER
         drawBackground(thePlayer.getX(),thePlayer.getY(),gc);
         
         //score displays
         gc.setFill(Color.WHITE);
         gc.fillText("Score is: " + (int)thePlayer.distance(origin), 10, 20);
         
         //reads the file containing the latest highscore
         try{
            Scanner scan = new Scanner(new File("highscore.txt"));
            highscore = scan.nextInt();
         }
         catch (FileNotFoundException fnfe) //created for the first instance of running the code
         {
            highscore = 0;
         }
         catch (NoSuchElementException nse) //if someone were to manually delete the highscore it would still run
         {
            highscore = 0;
         }
         
         //display the highscore   
         gc.fillText("Highscore is: " + highscore, 10, 40);
         
         //highscore keeper
         if ((int)thePlayer.distance(origin) > highscore)
         {
            highscore = (int)thePlayer.distance(origin); 
            newHighScore = true;
         }   
         
         //updates the highscore
         if (newHighScore)
         {
         try{
            FileOutputStream fos = new FileOutputStream("highscore.txt",false);
            PrintWriter pw = new PrintWriter(fos);
            pw.println(highscore);
            pw.close();
         }  
         catch(FileNotFoundException fnfe)
         {
            System.out.println("file not found");
         }}
         
         //losing
         for (int i=0; i < mines.size(); i++)
         {  
            double end = Math.sqrt((mines.get(i).getX()-thePlayer.getX())*(mines.get(i).getX()-thePlayer.getX())+(mines.get(i).getY()-thePlayer.getY())*(mines.get(i).getY()-thePlayer.getY()));
            if (end <= 20)
            {
               lose = true; //doesnt draw the player
               mines.remove(i); //removes the mine
               ta.stop(); //stops the animation
            }
         }
         
         //draws the player
         if (!lose)
         {
            thePlayer.draw(300,300,gc,true);
            thePlayer.act(left, right, up, down);
         }   
         
         //get the grid position of the Player
         cgridx = ((int)thePlayer.getX())/100;
         cgridy = ((int)thePlayer.getY())/100;
         
         //stores the grid positions of the player
         cposx.add(cgridx);
         cposy.add(cgridy);
         
         //declaring the lastgrids of the player
         int lastcgridx = 0;
         int lastcgridy = 0;
         int possx =0;
         int possy =0;
         
         //last grids of thePlayer
         possx = cposx.get(cposx.size()-1);
         possy = cposy.get(cposy.size()-1);
         if (cgridx != possx)
            lastcgridx = possx;
         if (cgridy != possy)   
            lastcgridy = possy;
         
         //runs if thePlayer has moved
         if (cgridx != lastcgridx || cgridy != lastcgridy) 
         {
            //left
            int y,x;
            for(int i = 0; i < 10; i++) //moves from grid to grid
            {
               x = cgridx-4;
               createNewMines(x,(cgridy+i)); //draws the mines from thePlayer position to the top of the screen
               createNewMines(x,(cgridy-i)); //draws the mines from thePlayer position to the bottom of the screen
            }
            //right
            for(int i =0; i <10; i++)
            {
               x = cgridx + 3;
               createNewMines(x,(cgridy+i));
               createNewMines(x,(cgridy-i));
            }
            //top
            for(int i =0; i <10; i++)
            {
               y = cgridy - 4;
               createNewMines((cgridx+i),y); //draws the mines from thePlayer position to the right end of the screen
               createNewMines((cgridx-i),y); //draws the mines from thePlayer position to the left end of the screen
            }
            //bottom
            for(int i =0; i <10; i++)
            {
               y = cgridy + 3;
               createNewMines((cgridx+i),y);
               createNewMines((cgridx-i),y);
            }
         }
         
         //goes through the mines added in the array and draws them
         for (int i =0; i < mines.size(); i++)
         {
           mines.get(i).advanceColor(); //adds animation color to the mines
           mines.get(i).draw(thePlayer.getX(),thePlayer.getY(),gc,false); //draws the mine
         }
         
         //deleting mines >= 800 away
         for (int i = 0; i < mines.size(); i++)
         {  
            //keeping track of a current mine
            Mine currentMine = new Mine(mines.get(i).getX(),mines.get(i).getY());
            //calculating the distance of thePlayer from the currentMine
            double dist = thePlayer.distance(currentMine);
            if (dist >= 800) //remove
               mines.remove(i);
         }      
      }
   }
   
   public class KeyListenerDown implements EventHandler<KeyEvent>  
   {
      public void handle(KeyEvent event) 
      { 
         if (event.getCode() == KeyCode.A) 
         {
            left = true;
         }
         if (event.getCode() == KeyCode.W)  
         {
            up = true;
         }
         if (event.getCode() == KeyCode.S)  
         {
            down = true;
         }
         if (event.getCode() == KeyCode.D)  
         {
            right = true;
         }
      }
   }
   
   public class KeyListenerUp implements EventHandler<KeyEvent>  
   {
      public void handle(KeyEvent event) 
      { 
         if (event.getCode() == KeyCode.A) 
         {
            left = false;
         }
         if (event.getCode() == KeyCode.W)  
         {
            up = false;
         }
         if (event.getCode() == KeyCode.S)  
         {
            down = false;
         }
         if (event.getCode() == KeyCode.D)  
         {
            right = false;
         }
      }
   }
   
   //create new mines method(x,y)
   float posx,posy;
   public void createNewMines(int x, int y)
   {
      int xgrid = x;
      int ygrid = y;
      
      //distance from origin
      double distance = Math.sqrt(((xgrid*100)-300)*((xgrid*100)-300)+((ygrid*100)-300)*((ygrid*100)-300));
      int n = (int)distance/1000;
      
      //creating mines with max of n
      for(int i = 0; i < n; i++)
      {
         float chance = rand.nextFloat(2);
         
         if(chance < 0.15) // 1/2 the chance bc the method is called twice for each side
         {
            //create mine at random pos in square
            posx = (xgrid*100) + (rand.nextFloat() * 100);
            posy = (ygrid*100) + (rand.nextFloat() * 100);
            Mine nMine = new Mine(posx,posy);
            
            //add mine
            mines.add(nMine);
         }
      }
   }
   
   public static void main(String[] args)
   {
      launch(args);
   }
}

