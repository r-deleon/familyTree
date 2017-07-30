/****************************************************************************
 **
 ** This demo file is part of yFiles for Java 3.0.0.4.
 **
 ** Copyright (c) 2000-2017 by yWorks GmbH, Vor dem Kreuzberg 28,
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ** yFiles demo files exhibit yFiles for Java functionalities. Any redistribution
 ** of demo files in source code or binary form, with or without
 ** modification, is not permitted.
 **
 ** Owners of a valid software license for a yFiles for Java version that this
 ** demo is shipped with are allowed to use the demo source code as basis
 ** for their own yFiles for Java powered applications. Use of such programs is
 ** governed by the rights and conditions as set out in the yFiles for Java
 ** license agreement.
 **
 ** THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESS OR IMPLIED
 ** WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 ** MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 ** NO EVENT SHALL yWorks BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 ** SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 ** TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 ** PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 ** LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 ** NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 ** SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **
 ***************************************************************************/
package familytree;

import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.geometry.IRectangle;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.Map;
import logic.Person;

/**
* A visual that displays a detailed version of the employee including the icon, full name, position, email, fax and phone number as
 * well as a special visual that depicts the current status of the employee.
*/
class DetailVisual extends OrgChartVisual {
  private static final Font font = new Font("Dialog", Font.PLAIN, 10);
  private static final Font font2 = new Font("Dialog", Font.ITALIC, 8);

  private Person person;
  /**
   * A visual that visualizes the status of the employee by colored circles.  /**
   * A visual that visualizes the status of the employee by colored circles.
   */
  
  public DetailVisual(Person persona, IRectangle layout, boolean isFocused, boolean isHovered) {
    super(layout, isFocused, isHovered);
    this.person = persona;
  }

  @Override
  public void paintContent(IRenderContext context, Graphics2D g) {
    final AffineTransform oldTransform = g.getTransform();
    final Paint oldPaint = g.getPaint();
    final Font oldFont = g.getFont();
    if (person !=null){
        g.drawString(person.getFirstName(), 5, 50);
    }
    
    g.setTransform(oldTransform);
    g.setPaint(oldPaint);
    g.setFont(oldFont);
  
  }

  /**
   * Paints the various properties of the employee such as the name, position, email etc.
   */
  private void paintProperties(Graphics2D g) {
    g.setPaint(Color.BLACK);
    g.setFont(font);
    g.setFont(font2);
    g.setFont(font);
  }

  /**
   * Paints the icon of the employee on the left side of the rectangle
   */
  private void paintIcon(Graphics2D g) {
    final AffineTransform oldTransform = g.getTransform();
    final RenderingHints oldHints = new RenderingHints((Map) g.getRenderingHints());

    try {
      g.translate(5, 15);
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    } finally {
      g.setTransform(oldTransform);
      g.setRenderingHints(oldHints);
    }

  }
 
}
