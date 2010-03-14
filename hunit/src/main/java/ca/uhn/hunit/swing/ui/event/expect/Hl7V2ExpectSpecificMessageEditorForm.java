/**
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the
 * specific language governing rights and limitations under the License.
 *
 * The Initial Developer of the Original Code is University Health Network. Copyright (C)
 * 2001.  All Rights Reserved.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * GNU General Public License (the  "GPL"), in which case the provisions of the GPL are
 * applicable instead of those above.  If you wish to allow use of your version of this
 * file only under the terms of the GPL and not to allow others to use your version
 * of this file under the MPL, indicate your decision by deleting  the provisions above
 * and replace  them with the notice and other provisions required by the GPL License.
 * If you do not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the GPL.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Hl7V2ExpectSpecificMessageEditorForm.java
 *
 * Created on 17-Oct-2009, 2:27:34 PM
 */

package ca.uhn.hunit.swing.ui.event.expect;

import ca.uhn.hunit.swing.controller.ctx.EventEditorContextController;
import ca.uhn.hunit.swing.controller.ctx.TestEditorController;
import ca.uhn.hunit.event.expect.Hl7V2ExpectSpecificMessageImpl;
import ca.uhn.hunit.msg.Hl7V2MessageImpl;
import ca.uhn.hunit.swing.ui.event.AbstractEventEditorForm;

/**
 *
 * @author James
 */
public class Hl7V2ExpectSpecificMessageEditorForm extends AbstractEventEditorForm {
    private static final long serialVersionUID = 1L;
    
    private Hl7V2ExpectSpecificMessageImpl myEvent;
    private EventEditorContextController myController;

    /** Creates new form Hl7V2ExpectSpecificMessageEditorForm */
    public Hl7V2ExpectSpecificMessageEditorForm() {
        myEvent = null;

        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        myReplyMessageButtonGroup = new javax.swing.ButtonGroup();
        myBaseEventEditorForm = new ca.uhn.hunit.swing.ui.event.BaseEventEditorForm();
        myBaseExpectMessageEditorForm = new ca.uhn.hunit.swing.ui.event.expect.BaseExpectMessageEditorForm();
        jLabel1 = new javax.swing.JLabel();
        myAckRadioButton = new javax.swing.JRadioButton();
        myCustomRadioButton = new javax.swing.JRadioButton();

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("ca/uhn/hunit/l10n/UiStrings"); // NOI18N
        jLabel1.setText(bundle.getString("eventeditor.reply_message")); // NOI18N

        myReplyMessageButtonGroup.add(myAckRadioButton);
        myAckRadioButton.setText(bundle.getString("eventeditor.reply_message.hl7.ack")); // NOI18N
        myAckRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myAckRadioButtonActionPerformed(evt);
            }
        });

        myReplyMessageButtonGroup.add(myCustomRadioButton);
        myCustomRadioButton.setText(bundle.getString("eventeditor.reply_message.custom")); // NOI18N
        myCustomRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myCustomRadioButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(myBaseEventEditorForm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(myBaseExpectMessageEditorForm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(myAckRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(myCustomRadioButton)
                .addGap(171, 171, 171))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(myBaseEventEditorForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(myBaseExpectMessageEditorForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(myAckRadioButton)
                    .addComponent(myCustomRadioButton)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void myAckRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myAckRadioButtonActionPerformed
        updateReplyType();
    }//GEN-LAST:event_myAckRadioButtonActionPerformed

    private void myCustomRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myCustomRadioButtonActionPerformed
        updateReplyType();
    }//GEN-LAST:event_myCustomRadioButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton myAckRadioButton;
    private ca.uhn.hunit.swing.ui.event.BaseEventEditorForm myBaseEventEditorForm;
    private ca.uhn.hunit.swing.ui.event.expect.BaseExpectMessageEditorForm myBaseExpectMessageEditorForm;
    private javax.swing.JRadioButton myCustomRadioButton;
    private javax.swing.ButtonGroup myReplyMessageButtonGroup;
    // End of variables declaration//GEN-END:variables


    /**
     * {@inheritDoc }
     */
    @Override
    public void setController(EventEditorContextController theController) {
        myEvent = (Hl7V2ExpectSpecificMessageImpl) theController.getEvent();
        myController = theController;

        myBaseEventEditorForm.setController(theController);
        myBaseExpectMessageEditorForm.setController(theController);

        if (myEvent.getReplyMessage() != null) {
            myCustomRadioButton.setSelected(true);
        } else {
            myAckRadioButton.setSelected(true);
        }
    }

    private void updateReplyType() {
        if (myAckRadioButton.isSelected()) {
            myEvent.setReplyMessage(null);
        } else if (myEvent.getReplyMessage() == null) {
            myEvent.setReplyMessage(new Hl7V2MessageImpl());
        }
    }

    @Override
    public void tearDown() {
        myBaseEventEditorForm.tearDown();
        myBaseExpectMessageEditorForm.tearDown();
    }

}
