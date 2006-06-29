/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 19, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.compatibility.algorithm;

import java.util.Dictionary;
import java.util.Hashtable;

import org.cishell.compatibility.datamodel.OldDataModelAdapter;
import org.cishell.compatibility.guibuilder.ParameterMapAdapter;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.datamodel.DataModel;
import org.osgi.service.metatype.MetaTypeProvider;

import edu.iu.iv.core.IVC;
import edu.iu.iv.core.UnsupportedModelException;
import edu.iu.iv.core.algorithm.AbstractAlgorithm;
import edu.iu.iv.core.algorithm.AlgorithmProperty;


public class AlgorithmAdapter extends AbstractAlgorithm {
    private AlgorithmFactory factory;
    private DataModel[] dm;
    private CIShellContext ciContext;
    
    public AlgorithmAdapter(String label, AlgorithmFactory factory, DataModel[] dm, CIShellContext ciContext) {
        this.factory = factory;
        this.dm = dm;
        this.ciContext = ciContext;
        
        MetaTypeProvider provider = factory.createParameters(dm);
        if (provider != null) {
            this.parameterMap = new ParameterMapAdapter(factory.createParameters(dm));
        }
        
        getProperties().setPropertyValue(AlgorithmProperty.LABEL, label);
    }

    /**
     * @see edu.iu.iv.core.algorithm.AbstractAlgorithm#execute()
     */
    public boolean execute() {
        Algorithm alg = factory.createAlgorithm(dm, makeDictionary(), ciContext);
        DataModel[] newData = alg.execute();
        
        if (newData != null) {
            for (int i=0; i < newData.length; i++) {
                try {
                    IVC.getInstance().addModel(new OldDataModelAdapter(newData[i]));
                } catch (UnsupportedModelException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return true;
    }  
    
    protected Dictionary makeDictionary() {
        if (parameterMap instanceof ParameterMapAdapter) {
            return ((ParameterMapAdapter) parameterMap).createDictionary();
        } else {
            return new Hashtable();
        }
    }
}
