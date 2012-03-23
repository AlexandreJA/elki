package experimentalcode.students.roedler.parallelCoordinates.visualizer;

/*
 This file is part of ELKI:
 Environment for Developing KDD-Applications Supported by Index-Structures

 Copyright (C) 2012
 Ludwig-Maximilians-Universität München
 Lehr- und Forschungseinheit für Datenbanksysteme
 ELKI Development Team

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;
import de.lmu.ifi.dbs.elki.result.Result;
import de.lmu.ifi.dbs.elki.visualization.VisualizationTask;
import de.lmu.ifi.dbs.elki.visualization.style.StyleLibrary;
import de.lmu.ifi.dbs.elki.visualization.svg.SVGPlot;
import de.lmu.ifi.dbs.elki.visualization.svg.SVGUtil;
import de.lmu.ifi.dbs.elki.visualization.visualizers.AbstractVisualization;
import experimentalcode.students.roedler.parallelCoordinates.projections.ProjectionParallel;

/**
 * Default class to handle parallel visualizations.
 * 
 * @author Robert Rödler
 * 
 * @param <NV> Vector type in relation
 */
public abstract class ParallelVisualization<NV extends NumberVector<?, ?>> extends AbstractVisualization {
  /**
   * The current projection
   */
  final protected ProjectionParallel proj;

  /**
   * The representation we visualize
   */
  final protected Relation<NV> relation;

  /**
   * margin
   */
  final double[] margins;

  /**
   * Space between two axes
   */
  protected double axsep;

  /**
   * viewbox size
   */
  final double[] size;

  /**
   * Constructor.
   * 
   * @param task Visualization task
   */
  public ParallelVisualization(VisualizationTask task) {
    super(task);
    this.proj = task.getProj();
    this.relation = task.getRelation();

    margins = new double[] { 0.05 * StyleLibrary.SCALE, 0.1 * StyleLibrary.SCALE, 0.05 * StyleLibrary.SCALE, 0.3 * StyleLibrary.SCALE };
    size = new double[] { task.width * StyleLibrary.SCALE, task.height * StyleLibrary.SCALE };
    axsep = size[0] / (proj.getInputDimensionality() - 1.);

    this.layer = setupCanvas(svgp, proj, task.getWidth(), task.getHeight());
  }

  /**
   * Utility function to setup a canvas element for the visualization.
   * 
   * @param svgp Plot element
   * @param proj Projection to use
   * @param width Width
   * @param height Height
   * @return wrapper element with appropriate view box.
   */
  public Element setupCanvas(SVGPlot svgp, ProjectionParallel proj, double width, double height) {
    Element layer = SVGUtil.svgElement(svgp.getDocument(), SVGConstants.SVG_G_TAG);
    final String transform = SVGUtil.makeMarginTransform(width, height, size[0], size[1], margins[0], margins[1], margins[2], margins[3]);
    SVGUtil.setAtt(layer, SVGConstants.SVG_TRANSFORM_ATTRIBUTE, transform);
    return layer;
  }

  protected double getSizeX() {
    return size[0];
  }

  protected double getSizeY() {
    return size[1];
  }

  protected double getMarginX() {
    return margins[0];
  }

  protected double getMarginY() {
    return margins[1];
  }

  protected void recalcAxisPositions() {
    axsep = size[0] / (proj.getVisibleDimensions() - 1.);
  }

  protected double getAxisHeight() {
    return StyleLibrary.SCALE;
  }

  protected double getAxisX(double d) {
    return d * axsep;
  }

  protected double[] getYPositions(DBID objId) {
    double[] v = proj.fastProjectDataToRenderSpace(relation.get(objId));
    Vector vec = new Vector(v);
    vec.timesEquals(getAxisHeight());
    return v;
  }
  
  @Override
  public void resultChanged(Result current) {
    super.resultChanged(current);
    if(current == proj) {
      synchronizedRedraw();
    }
  }
}