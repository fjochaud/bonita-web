/**
 * Copyright (C) 2011 BonitaSoft S.A.
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
package org.bonitasoft.console.server;

import org.bonitasoft.console.client.model.ConsoleFactoryCommon;
import org.bonitasoft.console.common.server.CommonAPIServlet;
import org.bonitasoft.console.server.datastore.bpm.flownode.FlowNodeConverter;
import org.bonitasoft.web.toolkit.client.ApplicationFactoryCommon;
import org.bonitasoft.web.toolkit.server.ApplicationFactoryServer;

/**
 * @author Séverin Moussel
 * 
 */
public class ConsoleAPIServlet extends CommonAPIServlet {

    private static final long serialVersionUID = 525945083859596909L;

    public ConsoleAPIServlet() {
        super();

        FlowNodeConverter.setFlowNodeConverter(new FlowNodeConverter());
    }

    @Override
    protected ApplicationFactoryCommon defineApplicatioFactoryCommon() {
        return new ConsoleFactoryCommon();
    }

    @Override
    protected ApplicationFactoryServer defineApplicatioFactoryServer() {
        return new ConsoleFactoryServer();
    }

}