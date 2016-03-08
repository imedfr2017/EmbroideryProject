package ewu.embroidit.parkc.fill;

import ewu.embroidit.parkc.io.StitchCode;
import ewu.embroidit.parkc.pattern.EmbStitch;
import ewu.embroidit.parkc.shape.A_EmbShapeWrapper;
import ewu.embroidit.parkc.util.VerticalLineSort;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/*-----------------------------------------------------------------------*/
/**
 *
 * @author Chris Park (christopherpark@eagles.ewu.edu)
 */
public class EmbFillTatamiRect extends A_EmbFill
{
    /*-----------------------------------------------------------------------*/
    
    /**
     * Constructs a Tatami stitch filling strategy.
     */
    public EmbFillTatamiRect()
    {
        super();
    }
    
    /*-----------------------------------------------------------------------*/
    
    /**
     * Implements a rectangle filling strategy for Tatami style fill stitches.
     * The resulting Line and EmbStitch Lists are added to the shape wrapper
     * sent as a parameter.
     * 
     * @param shapeWrapper shapeWrapper
     */
    @Override
    public void fillShape(A_EmbShapeWrapper shapeWrapper)
    {
        Rectangle rect;
        List<Line> lineList;
        
        rect = (Rectangle) shapeWrapper.getWrappedShape();
        lineList = new ArrayList<>();
        
        divideRectRecursive(rect, lineList);
        
        Collections.sort(lineList, new VerticalLineSort());
        shapeWrapper.setLineList(lineList);
        shapeWrapper.setStitchList(createStitchList(lineList));
    }
    
    /*-----------------------------------------------------------------------*/
    
    /**
     * Breaks a rectangle down into a list of line segments by subdividing the
     * rectangle until all resulting rectangles are under a defined width of 
     * approx. 3.78 pixels. Then the midpoint of each rectangle is used to
     * create a line segment.
     * 
     * @param rect Rectangle
     * @param lineList List&lt;Line&gt;
     */
    private void divideRectRecursive(Rectangle rect, List<Line> lineList)
    {   
        if(rect.getWidth() >= MM_TO_PXL * 2)
        {   
            Rectangle rectLeft, rectRight;
            
            rectLeft = new Rectangle(
                    rect.getX(),
                    rect.getY(),
                    rect.getWidth() / 2.0,
                    rect.getHeight());
            
            rectRight = new Rectangle(
                    rect.getX() + (rect.getWidth() / 2.0),
                    rect.getY(),
                    rect.getWidth() / 2.0,
                    rect.getHeight());
        
            divideRectRecursive(rectLeft, lineList);
            divideRectRecursive(rectRight, lineList);
        }
        
        Line midPointLine = new Line(
                rect.getX() + (rect.getWidth() / 2.0),
                rect.getY(),
                rect.getX() + (rect.getWidth() / 2.0),
                rect.getY() + rect.getHeight());
        
        lineList.add(midPointLine);
    }
    
    /*-----------------------------------------------------------------------*/
    
    /**
     * Breaks line segments for fill stitch down into list of individual stitches
     * alternating start end coordinates to reduce jump stitching in a fill.
     * 
     * NOTE: This doesn't account for stitch type or color appropriately yet.
     * 
     * @param lineList
     * @return stitchList List&lt;EmbStitch&gt;
     */
    public List<EmbStitch> createStitchList(List<Line> lineList)
    {
        boolean isOddStitch = true;
        List<EmbStitch> stitchList = new ArrayList<>();
        
        for(Line lineSegment: lineList)
        {
            if(isOddStitch)
            {
                stitchList.add(new EmbStitch(
                        new Point2D(lineSegment.getStartX(), lineSegment.getStartY()),
                        1, StitchCode.NORMAL));

                isOddStitch = false;
            }
            else
            {
                stitchList.add(new EmbStitch(
                        new Point2D(lineSegment.getEndX(), lineSegment.getEndY()),
                        1, StitchCode.NORMAL));
                
                isOddStitch = true;
            }
        }
        
        return stitchList;
    }
    
    /*-----------------------------------------------------------------------*/
}
