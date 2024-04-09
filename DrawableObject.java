import javafx.scene.paint.*;
import javafx.scene.canvas.*;

public abstract class DrawableObject
{
   public DrawableObject(float x, float y)
   {
      this.x = x;
      this.y = y;
   }

   //positions
   private float x;
   private float y;
   
   //takes the position of the player and calls draw me with appropriate positions
   public void draw(float playerx, float playery, GraphicsContext gc, boolean isPlayer)
   {
      //the 300,300 places the player at 300,300, if you want to change it you will have to modify it here
      
      if(isPlayer)
         drawMe(playerx,playery,gc);
      else
         drawMe(-playerx+300+x,-playery+300+y,gc);
   }
   
   //this method you implement for each object you want to draw. Act as if the thing you want to draw is at x,y.
   //NOTE: DO NOT CALL DRAWME YOURSELF. Let the the "draw" method do it for you. I take care of the math in that method for a reason.
   public abstract void drawMe(float x, float y, GraphicsContext gc);
   private float forceX = 0;
   private float forceY = 0;
   public void act(boolean left, boolean right, boolean up, boolean down)
   { 
      //adds speed in that direction
      if (left)
         forceX-= .1;
      if (right)
         forceX+= .1;
      if (down)
         forceY+=.1;
      if (up)
         forceY-=.1;
      
      //x speed parameters   
      if(left || right)
      {
         if (forceX > 5)
            forceX = 5;
         if (forceX < -5)
            forceX = -5;  
      } 
      //slowing down when not pressed
      if(!left && !right)
      {
         if (forceX > 0.25) //slowing down after d
            forceX -= 0.025;
         if (forceX < -0.25) //slowing down after a
            forceX += 0.025;   
         if (forceX < 0.25 && forceX > -0.25) //stop
            forceX = 0;  
      }
      //y speed parameters     
      if(up || down)
      {
         if (forceY > 5)
            forceY = 5;
         if (forceY < -5)
            forceY = -5;
      }
      //slowing down when not pressed
      if(!up && !down)
      {
         if (forceY > 0.25) //slowing down after s
         {
            forceY -=0.025;
         }   
         if (forceY < -0.25) //slowing down after w
            forceY += 0.025;
         if (forceY < 0.25 && forceY > -0.25) //stop
            forceY = 0;  
      } 
      
      //setting x & y
      setX(getX() + forceX);
      setY(getY() + forceY);  
   }
   
   public float getX(){return x;}
   public float getY(){return y;}
   public void setX(float x_){x = x_;}
   public void setY(float y_){y = y_;}
   
   public double distance(DrawableObject other)
   {
      return (Math.sqrt((other.x-x)*(other.x-x) +  (other.y-y)*(other.y-y)));
   }

}