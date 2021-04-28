/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.AssignedItemHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.LocationHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.BooleanColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.CustomColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.FloatColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.MachineItemRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.NameHierarchyColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class MachineImportHelperCommon {
    
    public static final String KEY_NAME = "name";
    public static final String KEY_INDENT = "indentLevel";
    public static final String KEY_PARENT = "importMdItem";
    public static final String KEY_SORT_ORDER = "importSortOrder";
    public static final String KEY_ASSIGNED_ITEM = "assignedItem";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_IS_TEMPLATE = "importIsTemplateString";
    public static final String KEY_TEMPLATE_INVOCATION = "importTemplateAndParameters";

    public static final String HEADER_PARENT = "Parent ID";
    public static final String HEADER_BASE_LEVEL = "Level";
    public static final String HEADER_NAME = "Name";
    public static final String HEADER_ALT_NAME = "Alternate Name";
    public static final String HEADER_DESCRIPTION = "Description";
    public static final String HEADER_SORT_ORDER = "Sort Order";
    public static final String HEADER_ASSIGNED_ITEM_DESCRIPTION = "Assigned Catalog/Inventory Item Description";
    public static final String HEADER_ASSIGNED_ITEM = "Assigned Catalog/Inventory Item";
    public static final String HEADER_LOCATION = "Location";
    public static final String HEADER_TEMPLATE = "Is Template?";
    public static final String HEADER_TEMPLATE_INVOCATION = "Template Instantiation";
    
    private String optionRootItemName = null;
    private ItemDomainMachineDesign rootItem = null;

    public static MachineItemRefColumnSpec parentItemColumnSpec(
            List<ColumnModeOptions> options,
            MachineImportHelperCommon helperCommon) {
        
        return new MachineItemRefColumnSpec(
                HEADER_PARENT,
                KEY_PARENT,
                "setImportMdItem",
                "CDB ID, name, or path of parent machine design item. Name must be unique and prefixed with '#'. Path must be prefixed with '#', start with a '/', and use '/' as a delimiter. If name includes an embedded '/' character, escape it by preceding with a '\' character.",
                null,
                options,
                helperCommon.getRootItem());
    }
    
    public static NameHierarchyColumnSpec nameHierarchyColumnSpec(List<ColumnModeOptions> options) {
        return new NameHierarchyColumnSpec(
                "Name hierarchy column",
                options,
                HEADER_BASE_LEVEL,
                KEY_NAME,
                KEY_INDENT,
                3);
    }

    public static StringColumnSpec nameColumnSpec(List<ColumnModeOptions> options) {
        return new StringColumnSpec(
                HEADER_NAME,
                "name",
                "setName",
                "Machine element name.",
                "getName",
                options,
                128);
    }
    
    public static StringColumnSpec altNameColumnSpec(List<ColumnModeOptions> options) {
        return new StringColumnSpec(
                HEADER_ALT_NAME,
                "alternateName",
                "setAlternateName",
                "Machine element alternate name.",
                "getAlternateName",
                options,
                128);
    }
    
    public static StringColumnSpec descriptionColumnSpec(List<ColumnModeOptions> options) {
        return new StringColumnSpec(
                HEADER_DESCRIPTION,
                "description",
                "setDescription",
                "Machine element description.",
                "getDescription",
                options,
                256);
    }
    
    public static FloatColumnSpec sortOrderColumnSpec(List<ColumnModeOptions> options) {
        return new FloatColumnSpec(
                HEADER_SORT_ORDER,
                KEY_SORT_ORDER,
                "setImportSortOrder",
                "Sort order within parent item (as decimal), defaults to order in input sheet when creating new items.",
                "getExportSortOrder",
                options);
    }
    
    public static StringColumnSpec assignedItemDescriptionColumnSpec(List<ColumnModeOptions> options) {
        return new StringColumnSpec(
                HEADER_ASSIGNED_ITEM_DESCRIPTION,
                "importAssignedItemDescription",
                "setImportAssignedItemDescription",
                "Description of catalog or inventory item assigned to machine element (for documentation purposes only).",
                null,
                options,
                256);
    }
    
    public static CustomColumnSpec assignedItemColumnSpec(List<ColumnModeOptions> options) {
        AssignedItemHandler assignedItemHandler = new AssignedItemHandler();
        return new CustomColumnSpec(
                HEADER_ASSIGNED_ITEM,
                "importAssignedItemString",
                "CDB ID or name of assigned catalog or inventory item. Name must be unique and prefixed with '#'.",
                "getAssignedItem",
                true,
                options,
                assignedItemHandler);
    }
    
    public static CustomColumnSpec locationColumnSpec(List<ColumnModeOptions> options) {
        LocationHandler locationHandler = new LocationHandler();        
        return new CustomColumnSpec(
                HEADER_LOCATION,
                "importLocationItemString",
                "CDB ID or name of CDB location item (use of word 'parent' allowed for documentation purposes, it is ignored). Name must be unique and prefixed with '#'.",
                "getExportLocation",
                true,
                options,
                locationHandler);
    }
    
    public static BooleanColumnSpec isTemplateColumnSpec(List<ColumnModeOptions> options) {
        return new BooleanColumnSpec(
                HEADER_TEMPLATE,
                KEY_IS_TEMPLATE,
                "setImportIsTemplate",
                "True/yes if item is template, false/no otherwise.",
                null,
                options);
    }
    
    public static StringColumnSpec templateInvocationColumnSpec(List<ColumnModeOptions> options) {
        return new StringColumnSpec(
                HEADER_TEMPLATE_INVOCATION,
                KEY_TEMPLATE_INVOCATION,
                "setImportTemplateAndParameters",
                "Template to instantiate with required parameters, e.g., 'PS-SR-S{nn}-CAB1(nn=24)'.",
                null,
                options,
                0);
    }
    
    public static HelperWizardOption rootMachineItemWizardOption() {
        return new HelperWizardOption(
                "Default Root Machine Item",
                "Root of machine hierarchy to locate items within, used to locate items by name without specifying full path.",
                "optionRootItemName",
                HelperOptionType.STRING,
                ImportMode.CREATE);
    }

    public String getOptionRootItemName() {
        return optionRootItemName;
    }

    public void setOptionRootItemName(String optionRootItemName) {
        this.optionRootItemName = optionRootItemName;
    }
    
    public ItemDomainMachineDesign getRootItem() {
        return rootItem;
    }
    
    public void reset() {
        rootItem = null;
        optionRootItemName = null;
    }
    
    public ValidInfo validateOptionRootItemName() {

        boolean isValid = true;
        String validString = "";

        if (optionRootItemName != null) {
            if ((!optionRootItemName.isEmpty())) {
                List<ItemDomainMachineDesign> topLevelMatches = new ArrayList<>();
                List<ItemDomainMachineDesign> matchingItems
                        = ItemDomainMachineDesignFacade.getInstance().findByName(optionRootItemName);
                for (ItemDomainMachineDesign item : matchingItems) {
                    if (item.getParentMachineDesign() == null) {
                        // top-level item matches name
                        topLevelMatches.add(item);
                    }
                }

                if (topLevelMatches.size() == 1) {
                    rootItem = topLevelMatches.get(0);
                } else if (topLevelMatches.size() == 0) {
                    isValid = false;
                    validString = "no matching top-level machine item with name: " + optionRootItemName;
                } else {
                    // more than one match
                    isValid = false;
                    validString = "multiple matching top-level machine items with name: " + optionRootItemName;
                }
            } else {
                // null out option if empty string
                optionRootItemName = null;
            }
        }

        return new ValidInfo(isValid, validString);
    }
}
