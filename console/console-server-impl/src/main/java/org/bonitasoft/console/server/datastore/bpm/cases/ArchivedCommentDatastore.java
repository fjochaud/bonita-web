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
package org.bonitasoft.console.server.datastore.bpm.cases;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bonitasoft.console.client.model.bpm.cases.ArchivedCommentItem;
import org.bonitasoft.console.common.server.datastore.CommonDatastore;
import org.bonitasoft.console.common.server.utils.SearchOptionsBuilderUtil;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.comment.ArchivedComment;
import org.bonitasoft.engine.bpm.comment.ArchivedCommentsSearchDescriptor;
import org.bonitasoft.engine.search.Order;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.session.InvalidSessionException;
import org.bonitasoft.web.toolkit.client.common.exception.api.APIException;
import org.bonitasoft.web.toolkit.client.common.exception.api.APISessionInvalidException;
import org.bonitasoft.web.toolkit.server.api.DatastoreHasSearch;
import org.bonitasoft.web.toolkit.server.search.ItemSearchResult;

/**
 * @author Paul AMAR
 * 
 */
public class ArchivedCommentDatastore extends CommonDatastore<ArchivedCommentItem, ArchivedComment> implements DatastoreHasSearch<ArchivedCommentItem> {

    /**
     * Conversion look up table to for sortable fields
     */
    private static final String[][] SORTABLE_FIELDS_LUT = {
            { ArchivedCommentItem.ATTRIBUTE_POST_DATE, ArchivedCommentsSearchDescriptor.POSTDATE },
    };

    public ArchivedCommentDatastore(final APISession engineSession) {
        super(engineSession);
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // C.R.U.D.
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * @see org.bonitasoft.web.toolkit.server.api.DatastoreHasSearch#search(int, int, java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public ItemSearchResult<ArchivedCommentItem> search(int page, int resultsByPage, String search, String orders, Map<String, String> filters) {
        try {
            final SearchOptionsBuilder builder = SearchOptionsBuilderUtil.buildSearchOptions(page, resultsByPage, "", search);
            adjustSearchBuilder(filters, builder);
            /**
             * Need to convert field name passed by API into sortable field provided by engine.
             */
            if (orders != null && !orders.isEmpty()) {
                String[] sort = orders.split(" ");
                for (int i = 0; i < SORTABLE_FIELDS_LUT.length; i++) {
                    if (sort[0].equals(SORTABLE_FIELDS_LUT[i][0])) {
                        builder.sort(SORTABLE_FIELDS_LUT[i][1], Order.valueOf(sort[1]));
                    }
                }
            }

            final SearchResult<ArchivedComment> result = TenantAPIAccessor.getProcessAPI(getEngineSession()).searchArchivedComments(builder.done());

            final List<ArchivedCommentItem> archivedCommentList = new ArrayList<ArchivedCommentItem>();
            for (final ArchivedComment item : result.getResult()) {
                final ArchivedCommentItem resultArchivedCommentItem = convertEngineToConsoleItem(item);
                archivedCommentList.add(resultArchivedCommentItem);
            }

            return new ItemSearchResult<ArchivedCommentItem>(page, resultsByPage,
                    result.getCount(), archivedCommentList);

        } catch (final InvalidSessionException e) {
            throw new APISessionInvalidException(e);
        } catch (final Exception e) {
            throw new APIException(e);
        }
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CONVERTS
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Convert ProcessItem filters into engine filers
     * 
     * @param filters
     * @param builder
     */
    private void adjustSearchBuilder(final Map<String, String> filters, final SearchOptionsBuilder builder) {
        addFilterToSearchBuilder(filters, builder, ArchivedCommentItem.ATTRIBUTE_PROCESS_INSTANCE_ID, ArchivedCommentsSearchDescriptor.PROCESS_INSTANCE_ID);
    }

    /*
     * (non-Javadoc)
     * @see org.bonitasoft.console.server.datastore.bpm.CommonDatastore#convertEngineToConsoleItem(java.io.Serializable)
     */
    @Override
    protected ArchivedCommentItem convertEngineToConsoleItem(final ArchivedComment item) {
        if (item == null) {
            return null;
        }

        final ArchivedCommentItem consoleItem = new ArchivedCommentItem();
        // Il faudra rajouter les get() des comments
        consoleItem.setId(item.getId());
        consoleItem.setUserId(item.getUserId());
        consoleItem.setProcessInstanceId(item.getProcessInstanceId());
        consoleItem.setPostDate(item.getPostDate());
        consoleItem.setArchivedDate(item.getArchiveDate());
        consoleItem.setContent(item.getContent());

        return consoleItem;
    }
}
