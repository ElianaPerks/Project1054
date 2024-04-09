import javafx.scene.paint.*;
import javafx.scene.canvas.*;

//mine
public class Mine extends DrawableObject
{
	//takes in its position
   public Mine(float x, float y)
   {
      super(x,y);
   }
   
   public int way = 1;
   public float colorValue;
   
   //changes miens colors btwn red & white
   public void advanceColor()
   {  
      colorValue += 0.1f * way;
      if(colorValue > 1)
      {
         colorValue = 1;
         way = -1;
      }
      if (colorValue < 0)
      {
         colorValue = 0;
         way = 1;
      }
   }
   
   //draws itself at the passed in x and y.
   public void drawMe(float x, float y, GraphicsContext gc)
   {
      gc.setFill(Color.WHITE.interpolate(Color.RED, colorValue));
      gc.fillOval(x-6,y-6,12,12);
   }
   
   
   
   
}
