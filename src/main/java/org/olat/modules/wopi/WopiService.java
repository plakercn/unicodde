/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.modules.wopi;

import java.io.File;

import org.olat.core.commons.services.vfs.VFSLeafEditorSecurityCallback;
import org.olat.core.commons.services.vfs.VFSMetadata;
import org.olat.core.id.Identity;
import org.olat.core.util.vfs.VFSLeaf;

/**
 * 
 * Initial date: 6 Mar 2019<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public interface WopiService {

	boolean fileExists(String fileId);

	File getFile(String fileId);

	VFSLeaf getVfsLeaf(String fileId);

	VFSMetadata getMetadata(String fileId);

	Access createAccess(VFSMetadata vfsMetadata, Identity identity, VFSLeafEditorSecurityCallback secCallback);

	Access getAccess(String accessToken);
	
	void deleteAccess(String accessToken);

	boolean hasAction(Discovery discovery, String actionName, String suffix);

	Action getAction(Discovery discovery, String actionName, String suffix);

}
