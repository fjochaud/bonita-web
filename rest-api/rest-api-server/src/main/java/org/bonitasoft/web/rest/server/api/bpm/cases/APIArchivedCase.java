/**
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.web.rest.server.api.bpm.cases;

import java.util.List;
import java.util.Map;

import org.bonitasoft.web.rest.model.bpm.cases.ArchivedCaseDefinition;
import org.bonitasoft.web.rest.model.bpm.cases.ArchivedCaseItem;
import org.bonitasoft.web.rest.server.api.ConsoleAPI;
import org.bonitasoft.web.rest.server.datastore.bpm.cases.ArchivedCaseDatastore;
import org.bonitasoft.web.rest.server.datastore.bpm.process.ProcessDatastore;
import org.bonitasoft.web.rest.server.datastore.organization.UserDatastore;
import org.bonitasoft.web.rest.server.framework.api.APIHasGet;
import org.bonitasoft.web.rest.server.framework.api.APIHasSearch;
import org.bonitasoft.web.rest.server.framework.search.ItemSearchResult;
import org.bonitasoft.web.toolkit.client.common.exception.api.APIException;
import org.bonitasoft.web.toolkit.client.data.APIID;
import org.bonitasoft.web.toolkit.client.data.item.Definitions;
import org.bonitasoft.web.toolkit.client.data.item.ItemDefinition;

/**
 * @author Séverin Moussel
 */
public class APIArchivedCase extends ConsoleAPI<ArchivedCaseItem> implements APIHasGet<ArchivedCaseItem>, APIHasSearch<ArchivedCaseItem> {

    @Override
    public ItemDefinition defineItemDefinition() {
        return Definitions.get(ArchivedCaseDefinition.TOKEN);
    }

    @Override
    public ArchivedCaseItem get(final APIID id) {
        return new ArchivedCaseDatastore(getEngineSession()).get(id);
    }

    @Override
    public String defineDefaultSearchOrder() {
        return "";
    }

    @Override
    public ItemSearchResult<ArchivedCaseItem> search(final int page, final int resultsByPage, final String search, final String orders,
            final Map<String, String> filters) {

        // Check that team manager and supervisor filters are not used together
        if (filters.containsKey(ArchivedCaseItem.FILTER_TEAM_MANAGER_ID) && filters.containsKey(ArchivedCaseItem.FILTER_SUPERVISOR_ID)) {
            throw new APIException("Can't set those filters at the same time : " + ArchivedCaseItem.FILTER_TEAM_MANAGER_ID + " and "
                    + ArchivedCaseItem.FILTER_SUPERVISOR_ID);
        }

        return new ArchivedCaseDatastore(getEngineSession()).search(page, resultsByPage, search, orders, filters);
    }

    @Override
    protected void fillDeploys(final ArchivedCaseItem item, final List<String> deploys) {
        if (isDeployable(ArchivedCaseItem.ATTRIBUTE_STARTED_BY_USER_ID, deploys, item)) {
            item.setDeploy(
                    ArchivedCaseItem.ATTRIBUTE_STARTED_BY_USER_ID,
                    new UserDatastore(getEngineSession()).get(item.getStartedByUserId()));
        }

        if (isDeployable(ArchivedCaseItem.ATTRIBUTE_PROCESS_ID, deploys, item)) {
            item.setDeploy(
                    ArchivedCaseItem.ATTRIBUTE_PROCESS_ID,
                    new ProcessDatastore(getEngineSession()).get(item.getProcessId()));
        }

    }

    @Override
    protected void fillCounters(final ArchivedCaseItem item, final List<String> counters) {
    }
}
