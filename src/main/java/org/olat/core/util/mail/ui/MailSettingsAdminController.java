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
package org.olat.core.util.mail.ui;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.user.UserEmailAdminController;

/**
 * 
 * Initial date: 30.10.2017<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class MailSettingsAdminController extends BasicController {
	
	private final UserEmailAdminController userEmailController;
	private final MailInboxAdminController mailInboxController;
	
	private VelocityContainer mainVC;

	public MailSettingsAdminController(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl);

		userEmailController = new UserEmailAdminController(ureq, getWindowControl());
		listenTo(userEmailController);
		
		mailInboxController = new MailInboxAdminController(ureq, this.getWindowControl());
		listenTo(mailInboxController);
		
		mainVC = createVelocityContainer("settings");
		mainVC.put("userEmail", userEmailController.getInitialComponent());
		mainVC.put("inbox", mailInboxController.getInitialComponent());
		
		putInitialPanel(mainVC);
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		//
	}
	
}