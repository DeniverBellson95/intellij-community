/*
 * Copyright 2006 Sascha Weinreuter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.intellij.plugins.xpathView.search;

import com.intellij.ide.util.scopeChooser.ScopeChooserCombo;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.ComboboxWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.Vector;

public class ScopePanel extends JPanel {

    @SuppressWarnings({ "FieldCanBeLocal", "UnusedDeclaration" })
    private JPanel myRoot;

    private JRadioButton myWholeProjectScope;

    private JRadioButton myModuleScope;
    private ComboBox myModuleSelection;

    private JRadioButton myDirectoryScope;
    private TextFieldWithBrowseButton myDirectory;
    private JCheckBox myRecursive;

    private JRadioButton myCustomScope;
    private ComboboxWithBrowseButton myCustomScopeSelection;

    private Project myProject;

    public ScopePanel(@NotNull Project project) {
        myProject = project;
    }

    public void initComponent(@Nullable Module currentModule, final SearchScope scope) {
        final ItemListener stateListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                myModuleSelection.setEnabled(myModuleScope.isSelected());
                myDirectory.setEnabled(myDirectoryScope.isSelected());
                myRecursive.setEnabled(myDirectoryScope.isSelected());
                myCustomScopeSelection.setEnabled(myCustomScope.isSelected());

                if (e.getStateChange() == ItemEvent.SELECTED) {
                    firePropertyChange("scope", null, getSelectedScope());
                }
            }
        };
        final ItemListener scopeListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    firePropertyChange("scope", null, getSelectedScope());
                }
            }
        };

        myWholeProjectScope.addItemListener(stateListener);
        myWholeProjectScope.setSelected(scope.getScopeType() == SearchScope.ScopeType.PROJECT);
        myModuleScope.addItemListener(stateListener);
        myModuleScope.setSelected(scope.getScopeType() == SearchScope.ScopeType.MODULE);
        myDirectoryScope.addItemListener(stateListener);
        myDirectoryScope.setSelected(scope.getScopeType() == SearchScope.ScopeType.DIRECTORY);
        myCustomScope.addItemListener(stateListener);
        myCustomScope.setSelected(scope.getScopeType() == SearchScope.ScopeType.CUSTOM);

        myModuleSelection.setModel(createModel(ModuleManager.getInstance(myProject).getModules()));
        myModuleSelection.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                final JLabel l = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    final Module m = ((Module)value);
                    l.setIcon(m.getModuleType().getNodeIcon(true));
                    l.setText(m.getName());
                }
                return l;
            }
        });

        Module m;
        if (scope.getModuleName() != null) {
            if ((m = ModuleManager.getInstance(myProject).findModuleByName(scope.getModuleName())) == null) {
                m = currentModule;
            }
        } else {
            m = currentModule;
        }
        if (m != null) {
            myModuleSelection.setSelectedItem(m);
        }

        myModuleSelection.addItemListener(scopeListener);

        ((ScopeChooserCombo)myCustomScopeSelection).init(myProject, true, true, scope.getScopeName());
        myCustomScopeSelection.getComboBox().addItemListener(scopeListener);

        myDirectory.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            protected void textChanged(DocumentEvent e) {
                firePropertyChange("scope", null, getSelectedScope());
            }
        });
        myDirectory.setText(scope.getPath());
        myDirectory.addBrowseFolderListener("Select Path", "Select Path", myProject, FileChooserDescriptorFactory.createSingleFolderDescriptor());

        myRecursive.setSelected(scope.isRecursive());
    }

    @SuppressWarnings({ "unchecked" })
    private static ComboBoxModel createModel(Object[] elements) {
        return new DefaultComboBoxModel(new Vector(Arrays.asList(elements)));
    }

    private void createUIComponents() {
        myRoot = this;
        myCustomScopeSelection = new ScopeChooserCombo();
    }

    @Nullable
    private String getDirectoryName() {
        final String s = myDirectory.getText();
        return s != null && s.length() > 0 ? s : null;
    }

    @Nullable
    private String getModuleName() {
        final Module module = ((Module)myModuleSelection.getSelectedItem());
        return module != null ? module.getName() : null;
    }

    @NotNull
    private SearchScope.ScopeType getScopeType() {
        if (myWholeProjectScope.isSelected()) return SearchScope.ScopeType.PROJECT;
        if (myModuleScope.isSelected()) return SearchScope.ScopeType.MODULE;
        if (myDirectoryScope.isSelected()) return SearchScope.ScopeType.DIRECTORY;
        if (myCustomScope.isSelected()) return SearchScope.ScopeType.CUSTOM;

        assert false : "Unknown Scope";
        return null;
    }

    public SearchScope getSearchScope() {
        final SearchScope scope = getSelectedScope();
        scope.setCustomScope(((ScopeChooserCombo)myCustomScopeSelection).getSelectedScope());
        return scope;
    }

    SearchScope getSelectedScope() {
        return new SearchScope(getScopeType(),
                getDirectoryName(), myRecursive.isSelected(),
                getModuleName(),
                ((ScopeChooserCombo)myCustomScopeSelection).getSelectedScopeName());
    }
}
