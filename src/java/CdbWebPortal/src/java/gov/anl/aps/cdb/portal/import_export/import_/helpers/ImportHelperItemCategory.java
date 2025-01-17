/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemCategoryController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperItemCategory extends ImportHelperBase<ItemCategory, ItemCategoryController> {

    @Override
    protected List<ColumnSpec> initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(existingItemIdColumnSpec());
        specs.add(deleteExistingItemColumnSpec());
        
        specs.add(new StringColumnSpec(
                "Name", 
                "name", 
                "setName", 
                "Name of user group", 
                "getName", 
                ColumnModeOptions.rdCREATErUPDATE(), 
                64));
        
        specs.add(new StringColumnSpec(
                "Description", 
                "description", 
                "setDescription", 
                "Description of user group", 
                "getDescription", 
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(domainItemTypeListColumnSpec(ColumnModeOptions.oCREATEoUPDATE()));

        return specs;
    } 
   
    @Override
    public boolean supportsModeUpdate() {
        return true;
    }

    @Override
    public boolean supportsModeDelete() {
        return true;
    }
    
    @Override
    public boolean supportsModeTransfer() {
        return true;
    }

    @Override
    public ItemCategoryController getEntityController() {
        return ItemCategoryController.getInstance();
    }
    
    @Override
    public String getFilenameBase() {
        return "ItemCategory (Technical System)";
    }
    
    @Override
    protected ItemCategory newInvalidUpdateInstance() {
        return new ItemCategory();
    }

    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        ItemCategory entity = getEntityController().createEntityInstance();
        return new CreateInfo(entity, true, "");
    }  
    
}
