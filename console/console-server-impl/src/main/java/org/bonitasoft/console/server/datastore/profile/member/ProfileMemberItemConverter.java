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
package org.bonitasoft.console.server.datastore.profile.member;

import org.bonitasoft.console.client.model.portal.profile.ProfileMemberItem;
import org.bonitasoft.console.server.datastore.converter.ItemConverter;
import org.bonitasoft.engine.profile.ProfileMember;

/**
 * @author Vincent Elcrin
 * 
 */
public class ProfileMemberItemConverter extends ItemConverter<ProfileMemberItem, ProfileMember> {

    @Override
    public ProfileMemberItem convert(ProfileMember profileMember) {
        ProfileMemberItem item = new ProfileMemberItem();
        item.setId(profileMember.getId());
        item.setProfileId(profileMember.getProfileId());
        item.setUserId(profileMember.getUserId());
        item.setRoleId(profileMember.getRoleId());
        item.setGroupId(profileMember.getGroupId());
        return item;
    }

}