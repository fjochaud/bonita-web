/**
 * Copyright (C) 2012 BonitaSoft S.A.
 * 
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
package org.bonitasoft.web.rest.server.engineclient;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.web.rest.server.APITestWithMock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

/**
 * @author Colin PUY
 */
public class ProcessEngineClientTest extends APITestWithMock {

    private static Integer BUNCH_SIZE = 10;
    
    @Mock
    private ProcessAPI processAPI;
    private ProcessEngineClient processEngineClient;
    
    @Before
    public void setUp() {
        initMocks(this);
        processEngineClient = new ProcessEngineClient(processAPI);
    }
    
    @Test
    public void deleteArchivedProcessInstancesByBunch_delete_archived_processes_instances_by_bunch() throws Exception {
        when(processAPI.deleteArchivedProcessInstances(1L, 0, BUNCH_SIZE))
            .thenReturn(BUNCH_SIZE.longValue(), BUNCH_SIZE.longValue(), 0L);
        
        processEngineClient.deleteArchivedProcessInstancesByBunch(1L, 10);
        
        verify(processAPI, times(3)).deleteArchivedProcessInstances(1L, 0, 10);
    }
    
    @Test
    public void deleteProcessInstancesByBunch_delete_archived_processes_instances_by_bunch() throws Exception {
        when(processAPI.deleteProcessInstances(1L, 0, BUNCH_SIZE))
            .thenReturn(BUNCH_SIZE.longValue(), BUNCH_SIZE.longValue(), 0L);
        
        processEngineClient.deleteProcessInstancesByBunch(1L, 10);
        
        verify(processAPI, times(3)).deleteProcessInstances(1L, 0, 10);
    }

}