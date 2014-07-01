/**
 * e-Science Central
 * Copyright (C) 2008-2013 School of Computing Science, Newcastle University
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation at:
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package com.connexience.server.ejb.smtp;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.messages.TextMessage;
import com.connexience.server.model.security.Ticket;

import javax.ejb.Remote;
import javax.mail.internet.InternetAddress;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: martyn
 * Date: 18-Nov-2009
 * Time: 15:03:39
 * To change this template use File | Settings | File Templates.
 */
@Remote
public interface SMTPRemote
{
  void sendMail(List<String> userIds, String subject, String content, String contentType) throws ConnexienceException;

  void sendMail(InternetAddress[] recipients, String subject, String content, String contentType) throws ConnexienceException;

  void sendTextMessageReceivedEmail(Ticket ticket, String userId, TextMessage textMessage) throws ConnexienceException;

  void sendResetPasswordEmail(Ticket ticket, String userId, String code, String websiteURL) throws ConnexienceException;
}
