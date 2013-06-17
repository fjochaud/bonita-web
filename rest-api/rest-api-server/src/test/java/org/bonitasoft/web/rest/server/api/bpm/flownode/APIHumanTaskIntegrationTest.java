package org.bonitasoft.web.rest.server.api.bpm.flownode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bonitasoft.test.toolkit.bpm.TestCase;
import org.bonitasoft.test.toolkit.bpm.TestHumanTask;
import org.bonitasoft.test.toolkit.bpm.TestProcessFactory;
import org.bonitasoft.test.toolkit.organization.TestUser;
import org.bonitasoft.test.toolkit.organization.TestUserFactory;
import org.bonitasoft.web.rest.api.model.bpm.flownode.HumanTaskItem;
import org.bonitasoft.web.rest.server.AbstractConsoleTest;
import org.bonitasoft.web.rest.server.api.bpm.flownode.APIHumanTask;
import org.bonitasoft.web.toolkit.client.data.APIID;
import org.bonitasoft.web.toolkit.server.search.ItemSearchResult;
import org.junit.Ignore;
import org.junit.Test;

public class APIHumanTaskIntegrationTest extends AbstractConsoleTest {

    public APIHumanTask apiHumanTask;

    private TestHumanTask testHumanTask;

    /*
     * (non-Javadoc)
     * @see org.bonitasoft.console.server.AbstractJUnitWebTest#webTestSetUp()
     */
    @Override
    public void consoleTestSetUp() throws Exception {
        this.testHumanTask = TestProcessFactory.getDefaultHumanTaskProcess()
                .addActor(TestUserFactory.getJohnCarpenter())
                .startCase()
                .getNextHumanTask();
        createAPIHumanTask();
    }

    /*
     * (non-Javadoc)
     * @see org.bonitasoft.test.toolkit.AbstractJUnitTest#getInitiator()
     */
    @Override
    protected TestUser getInitiator() {
        return TestUserFactory.getJohnCarpenter();
    }

    @Test
    public void testGetDatastore() {
        assertNotNull("Is not possible to retrieve the dataStore", this.apiHumanTask.getDefaultDatastore());
    }

    @Test
    public void testGetHumanTaskItem() throws Exception {

        final ArrayList<String> deploys = new ArrayList<String>();
        deploys.add(HumanTaskItem.ATTRIBUTE_PROCESS_ID);
        final ArrayList<String> counters = new ArrayList<String>();
        final APIID apiId = APIID.makeAPIID(this.testHumanTask.getId());
        final HumanTaskItem humanTaskItem = this.apiHumanTask.runGet(apiId, deploys, counters);
        assertEquals("Not possible to get the APIHUmanTaskItem ", humanTaskItem.getName(), this.testHumanTask.getName());
        assertEquals("Not possible to get the APIHUmanTaskItem ", humanTaskItem.getDescription(), this.testHumanTask.getDescription());
    }

    @Test
    public void testUpdateHumanTaskItem() throws Exception {

        final APIID apiId = APIID.makeAPIID(this.testHumanTask.getId());

        // Update the humanTaskItem attributes
        final HashMap<String, String> attributes = new HashMap<String, String>();
        attributes.put(HumanTaskItem.ATTRIBUTE_ASSIGNED_USER_ID,
                String.valueOf(TestUserFactory.getJohnCarpenter().getId()));
        final HumanTaskItem updateHumanTaskItem = this.apiHumanTask.update(apiId, attributes);
        assertNotSame("Attributes are not update", updateHumanTaskItem.getAssignedId(),
                TestUserFactory.getJohnCarpenter().getId());

    }

    @Test
    public void testSearch() throws Exception {
        // Set the filters
        final HashMap<String, String> filters = new HashMap<String, String>();
        filters.put(HumanTaskItem.ATTRIBUTE_ID, String.valueOf(this.testHumanTask.getId()));

        // Search the humanTaskItem
        final ArrayList<String> deploys = new ArrayList<String>();
        deploys.add(HumanTaskItem.ATTRIBUTE_PROCESS_ID);
        final ArrayList<String> counters = new ArrayList<String>();
        final HumanTaskItem foundHumanTaskItem = this.apiHumanTask.runSearch(0, 1, null, null, filters, deploys, counters).getResults().get(0);
        assertEquals("Can't search the humanTaskItem", this.testHumanTask.getName(), foundHumanTaskItem.getName());
    }

    @Test
    @Ignore("To be fixed")
    public void testSearchFailedTask() throws Exception {
        // Test for failed tasks
        final TestCase failedTestCase = TestProcessFactory.getMisconfiguredProcess()
                .addActor(getInitiator())
                .startCase();

        final TestHumanTask failedTestHumanTask = failedTestCase.getNextHumanTask()
                .assignTo(getInitiator())
                .execute()
                .waitState(HumanTaskItem.VALUE_STATE_FAILED);

        final HashMap<String, String> filters = new HashMap<String, String>();
        filters.put(HumanTaskItem.ATTRIBUTE_STATE, HumanTaskItem.VALUE_STATE_FAILED);
        filters.put(HumanTaskItem.ATTRIBUTE_CASE_ID, String.valueOf(failedTestCase.getId()));

        assertEquals("No Failed tasks" + failedTestHumanTask.getHumanTaskInstance().getState(), 1,
                this.apiHumanTask.runSearch(0, 1, null, null, filters, new ArrayList<String>(), new ArrayList<String>()).getResults().size());
    }

    @Test
    /**
     * Check that the paging system works fine
     */
    public void testHumanTaskItemSearchPaging() throws InterruptedException {

        // Setup : insert enough tasks to have 2 pages
        for (int i = 0; i < 15; i++) {
            try {
                TestProcessFactory.getDefaultHumanTaskProcess().startCase();
            } catch (final Exception e) {
                fail("Can't start process [" + e.getLocalizedMessage() + "]");
            }
        }

        // Setup: retrieve the needed APIs
        // this.apiHumanTask = new APIHumanTask();
        // final APIServletCall caller = new APIServletCall(mockHttpServletRequest, mockHttpServletResponse);
        // this.apiHumanTask.setCaller(caller);

        // Search for page 2 (1 in zero based)
        final ItemSearchResult<HumanTaskItem> search = this.apiHumanTask.runSearch(1, 10, null,
                this.apiHumanTask.defineDefaultSearchOrder(),
                new HashMap<String, String>(),
                new ArrayList<String>(), new ArrayList<String>());

        assertTrue(search.getResults().size() == 6);
        assertTrue(search.getTotal() == 16);
    }

    @Test
    /**
     * Check when assigned a task to me this task is in available list
     * @throws Exception 
     */
    public void testAssignedTaskInAvailable() throws Exception {
        this.testHumanTask.assignTo(TestUserFactory.getJohnCarpenter());

        final ArrayList<String> deploys = new ArrayList<String>();
        deploys.add(HumanTaskItem.ATTRIBUTE_PROCESS_ID);
        final ArrayList<String> counters = new ArrayList<String>();
        final HashMap<String, String> filters = new HashMap<String, String>();
        filters.put(HumanTaskItem.FILTER_USER_ID,
                String.valueOf(TestUserFactory.getJohnCarpenter().getId()));

        final List<HumanTaskItem> listHumanTaskItem = this.apiHumanTask.runSearch(0, 1, null, null, filters, deploys, counters).getResults();
        assertEquals("HumanTask assigned to me not in available list", 1, listHumanTaskItem.size());
    }

    /**
     * Initialize APIHumanTask
     * 
     * @throws Exception
     */
    private void createAPIHumanTask() throws Exception {
        this.apiHumanTask = new APIHumanTask();
        this.apiHumanTask.setCaller(getAPICaller(TestUserFactory.getJohnCarpenter().getSession(),
                "API/bpm/humanTask"));
    }

}