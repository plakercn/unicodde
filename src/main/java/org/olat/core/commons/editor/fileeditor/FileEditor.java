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
package org.olat.core.commons.editor.fileeditor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.olat.core.commons.services.vfs.VFSLeafEditor;
import org.olat.core.commons.services.vfs.VFSLeafEditorConfigs;
import org.olat.core.commons.services.vfs.VFSLeafEditorSecurityCallback;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.translator.Translator;
import org.olat.core.id.Identity;
import org.olat.core.util.Util;
import org.olat.core.util.vfs.VFSLeaf;
import org.olat.core.util.vfs.VFSLockApplicationType;
import org.olat.core.util.vfs.VFSLockManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * Initial date: 18 Mar 2019<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
@Service
public class FileEditor implements VFSLeafEditor {
	
	private static final List<String> HTML_EDITOR_SUFFIX = Arrays.asList("html", "htm");
	private static final List<String> TEXT_EDITOR_SUFFIX = Arrays.asList("txt", "css", "csv");
	
	@Autowired
	private VFSLockManager lockManager;

	@Override
	public boolean isEnable() {
		return true;
	}

	@Override
	public String getType() {
		return "OpenOLAT";
	}

	@Override
	public String getDisplayName(Locale locale) {
		Translator translator = Util.createPackageTranslator(FileEditor.class, locale);
		return translator.translate("editor.display.name");
	}

	@Override
	public boolean isSupportingFormat(String suffix, Mode mode) {
		// Both the HTML editor and the text editor supports view and edit
		return HTML_EDITOR_SUFFIX.contains(suffix) || TEXT_EDITOR_SUFFIX.contains(suffix)? true: false;
	}

	@Override
	public boolean isLockedForMe(VFSLeaf vfsLeaf, Mode mode, Identity identity) {
		if (Mode.EDIT.equals(mode)) {
			return lockManager.isLockedForMe(vfsLeaf, identity, VFSLockApplicationType.vfs, null);
		}
		return false;
	}

	@Override
	public Controller getRunController(UserRequest ureq, WindowControl wControl, Identity identity, VFSLeaf vfsLeaf,
			VFSLeafEditorSecurityCallback secCallback, VFSLeafEditorConfigs configs) {
		return new FileEditorController(ureq, wControl, vfsLeaf, secCallback, configs);
	}

}
