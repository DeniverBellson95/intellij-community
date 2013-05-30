package org.hanuna.gitalk.swing_ui.render;

import org.hanuna.gitalk.commit.Hash;
import org.hanuna.gitalk.graph.elements.Node;
import org.hanuna.gitalk.printmodel.GraphPrintCell;
import org.hanuna.gitalk.printmodel.ShortEdge;
import org.hanuna.gitalk.printmodel.SpecialPrintElement;
import org.hanuna.gitalk.ui.tables.GraphCommitCell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.TableModel;
import java.awt.event.MouseEvent;

import static org.hanuna.gitalk.swing_ui.render.Print_Parameters.*;

public class PositionUtil {
  private static float distance(int x1, int y1, int x2, int y2) {
    return (float)Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
  }

  public static boolean overUpEdge(ShortEdge edge, int x, int y) {
    float thick = THICK_LINE;
    int x1 = WIDTH_NODE * edge.getDownPosition() + WIDTH_NODE / 2;
    int y1 = HEIGHT_CELL / 2;
    int x2 = WIDTH_NODE * edge.getUpPosition() + WIDTH_NODE / 2;
    int y2 = -HEIGHT_CELL / 2;
    //return true;
    return (distance(x1, y1, x, y) + distance(x2, y2, x, y) < distance(x1, y1, x2, y2) + thick);
  }

  public static boolean overDownEdge(ShortEdge edge, int x, int y) {
    float thick = THICK_LINE;
    int x1 = WIDTH_NODE * edge.getUpPosition() + WIDTH_NODE / 2;
    int y1 = HEIGHT_CELL / 2;
    int x2 = WIDTH_NODE * edge.getDownPosition() + WIDTH_NODE / 2;
    int y2 = HEIGHT_CELL + HEIGHT_CELL / 2;
    return distance(x1, y1, x, y) + distance(x2, y2, x, y) < distance(x1, y1, x2, y2) + thick;
  }

  public static boolean overNode(int position, int x, int y) {
    int x0 = WIDTH_NODE * position + WIDTH_NODE / 2;
    int y0 = HEIGHT_CELL / 2;
    int r = CIRCLE_RADIUS;
    return distance(x0, y0, x, y) <= r;
  }

  public static int getYInsideRow(MouseEvent e) {
    return e.getY() - getRowIndex(e) * HEIGHT_CELL;
  }

  public static int getRowIndex(MouseEvent e) {
    return e.getY() / HEIGHT_CELL;
  }

  @NotNull
  public static GraphPrintCell getGraphPrintCell(MouseEvent e, TableModel model) {
    int rowIndex = getRowIndex(e);
    return getGraphPrintCell(model, rowIndex);
  }

  @NotNull
  public static GraphPrintCell getGraphPrintCell(TableModel model, int rowIndex) {
    GraphCommitCell commitCell = (GraphCommitCell)model.getValueAt(rowIndex, 0);
    return commitCell.getPrintCell();
  }


  @Nullable
  public static Node getNode(MouseEvent e, TableModel model) {
    GraphPrintCell cell = getGraphPrintCell(e, model);
    return getNode(cell);
  }

  @Nullable
  public static Node getNode(GraphPrintCell cell) {
    for (SpecialPrintElement element : cell.getSpecialPrintElements()) {
      Node node = element.getGraphElement().getNode();
      if (node != null) {
        return node;
      }
    }
    return null;
  }

  @Nullable
  public static Hash getCommit(MouseEvent e, TableModel model) {
    Node node = getNode(e, model);
    if (node != null) {
      return node.getCommitHash();
    }
    return null;
  }
}
