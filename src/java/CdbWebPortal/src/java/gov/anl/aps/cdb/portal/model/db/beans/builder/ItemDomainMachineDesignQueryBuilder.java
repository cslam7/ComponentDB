/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans.builder;

import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 */
public class ItemDomainMachineDesignQueryBuilder extends ItemQueryBuilder {
    
    public ItemDomainMachineDesignQueryBuilder(Integer domainId, Map filterMap) {
        super(domainId, filterMap, null, null, null); 
    }

    public ItemDomainMachineDesignQueryBuilder(Integer domainId, Map filterMap, String sortField, SortOrder sortOrder, ItemSettings scopeSettings) {
        super(domainId, filterMap, sortField, sortOrder, scopeSettings);
    }
    
}
