package experimentalcode.erich.parallel.mapper;

/*
 This file is part of ELKI:
 Environment for Developing KDD-Applications Supported by Index-Structures

 Copyright (C) 2014
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
import de.lmu.ifi.dbs.elki.database.ids.DBIDRef;
import de.lmu.ifi.dbs.elki.database.ids.KNNList;
import de.lmu.ifi.dbs.elki.database.query.knn.KNNQuery;
import experimentalcode.erich.parallel.MapExecutor;
import experimentalcode.erich.parallel.SharedObject;

/**
 * Mapper to compute the kNN
 * 
 * @author Erich Schubert
 * 
 * @param <O> Object type
 */
public class KNNMapper<O> implements Mapper {
  /**
   * K parameter
   */
  int k;

  /**
   * KNN query object
   */
  KNNQuery<O> knnq;

  /**
   * Output channel to write to
   */
  SharedObject<KNNList> out;

  /**
   * Constructor.
   * 
   * @param k K parameter
   * @param knnq Distance query to use
   */
  public KNNMapper(int k, KNNQuery<O> knnq) {
    super();
    this.k = k;
    this.knnq = knnq;
  }

  /**
   * Connect the output channel.
   * 
   * @param output Output channel
   */
  public void connectKNNOutput(SharedObject<KNNList> output) {
    this.out = output;
  }

  @Override
  public Instance<O> instantiate(MapExecutor mapper) {
    return new Instance<>(k, knnq, out.instantiate(mapper));
  }

  @Override
  public void cleanup(Mapper.Instance inst) {
    // Nothing to do.
  }

  /**
   * Mapper instance for precomputing the kNN.
   * 
   * @author Erich Schubert
   */
  public static class Instance<O> implements Mapper.Instance {
    /**
     * k Parameter
     */
    int k;

    /**
     * kNN query
     */
    KNNQuery<O> knnq;

    /**
     * Output data store
     */
    SharedObject.Instance<KNNList> out;

    /**
     * Constructor.
     * 
     * @param k K parameter
     * @param knnq KNN query
     * @param out Output channel to write to
     */
    protected Instance(int k, KNNQuery<O> knnq, SharedObject.Instance<KNNList> out) {
      super();
      this.k = k;
      this.knnq = knnq;
      this.out = out;
    }

    @Override
    public void map(DBIDRef id) {
      out.set(knnq.getKNNForDBID(id, k));
    }
  }
}