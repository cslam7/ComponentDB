/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author craig
 */
public class ImportHelperMachineDesign extends ImportHelperBase<ItemDomainMachineDesign, ItemDomainMachineDesignController> {

    private static final Logger LOGGER = LogManager.getLogger(ImportHelperMachineDesign.class.getName());

    protected static String completionUrlValue = "/views/itemDomainMachineDesign/list?faces-redirect=true";
    
    protected Map<String, ItemDomainMachineDesign> itemByNameMap = new HashMap<>();
    
    @Override
    protected void createColumnModels_() {
        columns.add(new ImportHelperBase.BooleanColumnModel("Is Template", "isItemTemplate", "setImportIsTemplate", true, "Specifies whether this item is a template (true or false)."));
        columns.add(new ImportHelperBase.IdRefColumnModel("Container Id", "importContainerString", "setImportContainerItem", false, "Numeric ID of CDB machine design item that contains this new machine design hierarchy.", ItemDomainMachineDesignController.getInstance(), ItemDomainMachineDesign.class));
        columns.add(new ImportHelperBase.StringColumnModel("Path", "importPath", "setImportPath", false, "Path to new machine design item within container (/ separated). If path is empty, new item will be created directly in specified container.", 700));
        columns.add(new ImportHelperBase.StringColumnModel("Name", "name", "setName", true, "Machine design item name.", 128));
        columns.add(new ImportHelperBase.IdOrNameRefColumnModel("Project", "itemProjectString", "setProjectValue", true, "Numeric ID or name of CDB project.", ItemProjectController.getInstance(), ItemProject.class, null));
        columns.add(new ImportHelperBase.StringColumnModel("Alt Name", "alternateName", "setAlternateName", false, "Alternate machine design item name.", 32));
        columns.add(new ImportHelperBase.StringColumnModel("Description", "description", "setDescription", false, "Textual description of machine design item.", 256));
        columns.add(new ImportHelperBase.IdRefColumnModel("Assigned Catalog Item Id", "importAssignedCatalogItemString", "setImportAssignedCatalogItem", false, "Numeric ID of assigned catalog item.", ItemDomainCatalogController.getInstance(), ItemDomainCatalog.class));
        columns.add(new ImportHelperBase.IdRefColumnModel("Assigned Inventory Item Id", "importAssignedInventoryItemString", "setImportAssignedInventoryItem", false, "Numeric ID of assigned inventory item.", ItemDomainInventoryController.getInstance(), ItemDomainInventory.class));
        columns.add(new ImportHelperBase.IdOrNameRefColumnModel("Location", "importLocationItemString", "setImportLocationItem", false, "Numeric Id or name of CDB location item, for top-level non-template items only.", ItemDomainLocationController.getInstance(), ItemDomainLocation.class, null));
    }
    
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
    }
    
    @Override
    public ItemDomainMachineDesignController getEntityController() {
        return ItemDomainMachineDesignController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Machine Design Template";
    }
    
    @Override
    protected ItemDomainMachineDesign createEntityInstance() {
        return getEntityController().createEntityInstance();
    }
    
    /**
     * Set parent/child relationships between items, and create new item at
     * specified path within container item.  The path contains the names of 
     * the parent nodes between the new item and the container item, separated by
     * Unix style slashes.
     */
    @Override
    protected ValidInfo postParseRow(ItemDomainMachineDesign item, String id) {
        
        String methodLogName = "postParseRow() ";
        ItemDomainMachineDesign parentItem = null;
        
        // find parent item
        String path = item.getImportPath();
        if ((path == null) || (path.isEmpty())) {
            // no path is specified, see if there is a container machine design item
            ItemDomainMachineDesign container = item.getImportContainerItem();
            if (container == null) {
                // new item is a top-level machine design with no parent
                String msg = "creating top-level machine design item: " + item.getName();
                LOGGER.debug(methodLogName + msg);
                parentItem = null;
            } else {
                parentItem = container;
            }
            
        } else {
            // path is specified
            String[] nodeNames = path.split("/");
            if (nodeNames.length == 0) {
                // path is invalid after tokenization
                String msg = "invalid path specified: " + path;
                LOGGER.info(methodLogName + msg);
                return new ValidInfo(false, msg);
                
            } else {
                // path contains valid tokens, find parent whose name matches
                // last token
                String parentNodeName = nodeNames[nodeNames.length - 1].trim();
                parentItem = itemByNameMap.get(parentNodeName);
                if (parentItem == null) {
                    // no item with that name
                    String msg = "no parent with name: " + parentNodeName;
                    LOGGER.info(methodLogName + msg);
                    return new ValidInfo(false, msg);
                }
            }
        }
        
        // check for template restrictions
        if (item.getIsItemTemplate()) {
            
            if ((item.getImportAssignedCatalogItem() != null) || 
                    (item.getImportAssignedInventoryItem() != null)) {
                // template not allowed to have assigned catalog/inventory
                String msg = "Template cannot have assigned catalog/inventory item";
                LOGGER.info(methodLogName + msg);
                return new ValidInfo(false, msg);
            }
            
            if (item.getImportLocationItem() != null) {
                // template not allowed to have location
                String msg = "Template cannot have location item";
                LOGGER.info(methodLogName + msg);
                return new ValidInfo(false, msg);
            }
        }
        
        if (parentItem != null) {
            
            if (!Objects.equals(item.getIsItemTemplate(), parentItem.getIsItemTemplate())) {
                // parent and child must both be templates or both not be
                String msg = "parent and child must both be templates or both not be templates";
                LOGGER.info(methodLogName + msg);
                return new ValidInfo(false, msg);
            }
            
            if (item.getImportLocationItem() != null) {
                // only top-level item can have location
                String msg = "Only top-level item can have location";
                LOGGER.info(methodLogName + msg);
                return new ValidInfo(false, msg);
            }
            
            // establish parent/child relationship
            item.applyImportValues(parentItem);
        }
        
        // add entry to name map for new item
        itemByNameMap.put(item.getName(), item);
        
        return new ValidInfo(true, "");
    }

}