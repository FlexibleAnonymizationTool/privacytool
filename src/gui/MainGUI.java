/*
* Copyright (C) 2015 "IMIS-Athena R.C.",
* Institute for the Management of Information Systems, part of the "Athena"
* Research and Innovation Centre in Information, Communication and Knowledge Technologies.
* [http://www.imis.athena-innovation.gr/]
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package privacytool.gui;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import privacytool.framework.anonymizationrules.AnonymizationRules;
import privacytool.framework.hierarchy.Hierarchy;
import privacytool.gui.data.QuasiIdentifiersPanel;
import privacytool.gui.hierarchy.HierarchyAutogeneratePanel;
import privacytool.gui.anonymizationrules.ImportRulesPanel;

/**
 *
 * @author serafeim
 */
public class MainGUI extends javax.swing.JFrame {
    
    /**
     * Creates new form MainGUI
     */
    
    public MainGUI() {
        initComponents();
        this.algorithmsPanel1.setResultsPanel(this.solutionsPanel1);
        this.solutionsPanel1.setAnonymizedDatasetPanel(this.anonymizedDatasetPanel2);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Tabs = new javax.swing.JTabbedPane();
        TransforamtionTab = new javax.swing.JPanel();
        inputDataPanel1 = new privacytool.gui.transformationTab.InputDataPanel();
        hierarchyPanel1 = new privacytool.gui.transformationTab.HierarchyPanel();
        algorithmsPanel1 = new privacytool.gui.transformationTab.AlgorithmsPanel();
        SolutionsTab = new javax.swing.JPanel();
        solutionsPanel1 = new privacytool.gui.solutionsTab.SolutionsPanel();
        ResultsTab = new javax.swing.JPanel();
        anonymizedDatasetPanel2 = new privacytool.gui.resultsTab.AnonymizedDatasetPanel();
        MenuBar = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        Data = new javax.swing.JMenu();
        ImportData = new javax.swing.JMenuItem();
        SetQuasi_ids = new javax.swing.JMenuItem();
        Hierarchy = new javax.swing.JMenu();
        ImportHierarchy = new javax.swing.JMenuItem();
        ExportHierarchy = new javax.swing.JMenuItem();
        AutogenerateHierarchy = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Privacy Tool");

        hierarchyPanel1.setMinimumSize(new java.awt.Dimension(500, 502));

        javax.swing.GroupLayout TransforamtionTabLayout = new javax.swing.GroupLayout(TransforamtionTab);
        TransforamtionTab.setLayout(TransforamtionTabLayout);
        TransforamtionTabLayout.setHorizontalGroup(
            TransforamtionTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TransforamtionTabLayout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addComponent(inputDataPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 858, Short.MAX_VALUE)
                .addGap(67, 67, 67)
                .addGroup(TransforamtionTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hierarchyPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
                    .addComponent(algorithmsPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(67, 67, 67))
        );
        TransforamtionTabLayout.setVerticalGroup(
            TransforamtionTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TransforamtionTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TransforamtionTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inputDataPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(TransforamtionTabLayout.createSequentialGroup()
                        .addComponent(hierarchyPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(algorithmsPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15))
        );

        Tabs.addTab("Transformation", TransforamtionTab);

        javax.swing.GroupLayout SolutionsTabLayout = new javax.swing.GroupLayout(SolutionsTab);
        SolutionsTab.setLayout(SolutionsTabLayout);
        SolutionsTabLayout.setHorizontalGroup(
            SolutionsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SolutionsTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(solutionsPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1606, Short.MAX_VALUE)
                .addContainerGap())
        );
        SolutionsTabLayout.setVerticalGroup(
            SolutionsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SolutionsTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(solutionsPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        Tabs.addTab("Solutions", SolutionsTab);

        javax.swing.GroupLayout ResultsTabLayout = new javax.swing.GroupLayout(ResultsTab);
        ResultsTab.setLayout(ResultsTabLayout);
        ResultsTabLayout.setHorizontalGroup(
            ResultsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ResultsTabLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(anonymizedDatasetPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1598, Short.MAX_VALUE)
                .addContainerGap())
        );
        ResultsTabLayout.setVerticalGroup(
            ResultsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ResultsTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(anonymizedDatasetPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE)
                .addContainerGap())
        );

        Tabs.addTab("Results", ResultsTab);

        getContentPane().add(Tabs, java.awt.BorderLayout.CENTER);

        FileMenu.setText("File");

        Data.setText("Data");

        ImportData.setText("Import");
        ImportData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportDataActionPerformed(evt);
            }
        });
        Data.add(ImportData);

        SetQuasi_ids.setText("Set hierarchies to QIs");
        SetQuasi_ids.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SetQuasi_idsActionPerformed(evt);
            }
        });
        Data.add(SetQuasi_ids);

        FileMenu.add(Data);

        Hierarchy.setText("Hierarchy");

        ImportHierarchy.setText("Import");
        ImportHierarchy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportHierarchyActionPerformed(evt);
            }
        });
        Hierarchy.add(ImportHierarchy);

        ExportHierarchy.setText("Export");
        ExportHierarchy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportHierarchyActionPerformed(evt);
            }
        });
        Hierarchy.add(ExportHierarchy);

        AutogenerateHierarchy.setText("Autogenerate");
        AutogenerateHierarchy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AutogenerateHierarchyActionPerformed(evt);
            }
        });
        Hierarchy.add(AutogenerateHierarchy);

        FileMenu.add(Hierarchy);

        jMenu1.setText("Anonymization Rules");

        jMenuItem4.setText("Import");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem5.setText("Export");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        FileMenu.add(jMenu1);

        jMenuItem1.setText("Save anonymized Dataset");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        FileMenu.add(jMenuItem1);

        MenuBar.add(FileMenu);

        jMenu2.setText("Edit");
        MenuBar.add(jMenu2);

        setJMenuBar(MenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ImportDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportDataActionPerformed
        System.out.println("Importing Data...");
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);
        File file = null;
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            //TODO: OPEN FILE HERE
            System.out.println("Opening: " + file.getAbsolutePath());
        } else {
            System.out.println("Open command cancelled by user");
        }
        if(file != null){
            try {
                inputDataPanel1.initTable(file,null);
            } catch (IOException ex) {
                Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            hierarchyPanel1.setDataset(inputDataPanel1.getData());
            algorithmsPanel1.setDataset(inputDataPanel1.getData());
        }

    }//GEN-LAST:event_ImportDataActionPerformed

    private void AutogenerateHierarchyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AutogenerateHierarchyActionPerformed
        
        HierarchyAutogeneratePanel autogeneratePanel = new HierarchyAutogeneratePanel(inputDataPanel1.getData());
        
        int result = JOptionPane.showConfirmDialog(null, autogeneratePanel,
                "Autogenerate Hierarchy", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            //System.out.println("mainGUI - hierarchyType: " + autogeneratePanel.hierarchyType);
            if(autogeneratePanel.hierarchyType.equals("distinct")){
                String name = null;
                String nodesType = null;
                String attribute = null;
                String sorting = null;
                int fanout = 0;
                int plusMinusFanout = 0;
                //boolean exactbutton = false;
                
                for (HierarchyAutogeneratePanel.DistinctField fieldTitle : HierarchyAutogeneratePanel.DistinctField.values()) {
                    //System.out.println(fieldTitle.getTitle() + " " + autogeneratePanel.getFieldText(fieldTitle));
                    
                    switch (fieldTitle.getTitle()) {
                        case "Name":
                            if(autogeneratePanel.getFieldText(fieldTitle).isEmpty()){
                                ErrorWindow.showErrorWindow("Please give an Hierarchy Name and try again");
                                return;
                            }
                            name = autogeneratePanel.getFieldText(fieldTitle).trim();
                            break;
                        case "Nodes Type":
                            nodesType = autogeneratePanel.getFieldText(fieldTitle);
                            break;
                        case "on Attribute":
                            if(autogeneratePanel.getFieldText(fieldTitle) == null){
                                ErrorWindow.showErrorWindow("Please load dataset and try again");
                                return;
                            }
                            attribute = autogeneratePanel.getFieldText(fieldTitle);
                            break;
                        case "Sorting":
                            sorting = autogeneratePanel.getFieldText(fieldTitle);
                            break;
                        case "Fanout":
                            try {
                                fanout = Integer.parseInt(autogeneratePanel.getFieldText(fieldTitle).trim());
                            }catch(java.lang.NumberFormatException e){
                                ErrorWindow.showErrorWindow("Please give a valid fanout number");
                                return;
                            }
                            if(fanout <= 1){
                                ErrorWindow.showErrorWindow("Fanout must be a positive number > 1");
                                return;
                            }
                            break;
                            /*
                            case "Exact Button":
                            if(autogeneratePanel.getFieldText(fieldTitle).equals("selected")){
                            exactbutton = true;
                            }
                            break;
                            */
                        case "+/-":
                            try {
                                plusMinusFanout = Integer.parseInt(autogeneratePanel.getFieldText(fieldTitle).trim());
                            }catch(java.lang.NumberFormatException e){
                                ErrorWindow.showErrorWindow("Please give a valid fanout +/- number");
                                return;
                            }
                            
                            //                            System.out.println(plusMinusFanout + " " + fanout/2);
                            if(plusMinusFanout < 0 || (plusMinusFanout >= fanout/2)){
                                ErrorWindow.showErrorWindow("Fanout +/- must be a positive number < fanout/2");
                                return;
                            }
                            break;
                    }
                }
                //System.out.println();
                //                System.out.println("name: " + name +"\n" + "nodesType: " + nodesType + "\n" + "attribute: " + attribute + "\n" +
                //                        "sorting: " + sorting + "\n" + "fanout: " + fanout + "\n" + "plusMinusFanout: " + plusMinusFanout);
                //                System.out.println();
                hierarchyPanel1.autogenerateDistinctHierarchy(name, nodesType, attribute, sorting, fanout, plusMinusFanout);
            } else if (autogeneratePanel.hierarchyType.equals("ranges")){
                String name = null;
                String nodesType = null;
                Double start = null;
                Double end = null;
                Double step = null;
                int fanout = 0;
                int plusMinusFanout = 0;
                //boolean exactbutton = false;
                
                for (HierarchyAutogeneratePanel.RangeField fieldTitle : HierarchyAutogeneratePanel.RangeField.values()) {
                    //System.out.println(fieldTitle.getTitle() + " " + autogeneratePanel.getFieldText(fieldTitle));
                    switch (fieldTitle.getTitle()) {
                        case "Name":
                            if(autogeneratePanel.getFieldText(fieldTitle).isEmpty()){
                                ErrorWindow.showErrorWindow("Please give an Hierarchy Name and try again");
                                return;
                            }
                            name = autogeneratePanel.getFieldText(fieldTitle);
                            break;
                        case "Nodes Type":
                            nodesType = autogeneratePanel.getFieldText(fieldTitle);
                            break;
                        case "Start-End":
                            if(autogeneratePanel.getFieldText(fieldTitle).isEmpty() || !autogeneratePanel.getFieldText(fieldTitle).contains("-")){
                                ErrorWindow.showErrorWindow("Please give valid start - end");
                                return;
                            }
                            String domain = autogeneratePanel.getFieldText(fieldTitle);
                            String[] parts = domain.split("-");
                            
                            try {
                                start = (Double)Double.parseDouble(parts[0].trim());
                                end = (Double)Double.parseDouble(parts[1].trim());
                                if(nodesType.equals("int") && (start % 1 != 0 || end % 1 != 0)){
                                    ErrorWindow.showErrorWindow("Start-end are not integer numbers, please try again");
                                    return;
                                }
                                if(end <= start){
                                    ErrorWindow.showErrorWindow("Wrong start, end values! End value should be greater than start");
                                    return;
                                }
                            }catch(java.lang.NumberFormatException e){
                                ErrorWindow.showErrorWindow("Start-end are not valid integer numbers, please try again");
                                return;
                            }
                            break;
                            
                        case "Step":
                            try {
                                step = Double.parseDouble(autogeneratePanel.getFieldText(fieldTitle));
                                if(nodesType.equals("int") && (step % 1 != 0)){
                                    ErrorWindow.showErrorWindow("Step not an integer number");
                                    return;
                                }
                            }catch(java.lang.NumberFormatException e){
                                ErrorWindow.showErrorWindow("Please give a valid step number");
                                return;
                            }
                            if(step <= 0){
                                ErrorWindow.showErrorWindow("Step must be a positive number");
                                return;
                            }
                            
                            break;
                        case "Fanout":
                            try {
                                fanout = Integer.parseInt(autogeneratePanel.getFieldText(fieldTitle));
                            }catch(java.lang.NumberFormatException e){
                                ErrorWindow.showErrorWindow("Please give a valid fanout number");
                                return;
                            }
                            if(fanout <= 1){
                                ErrorWindow.showErrorWindow("Fanout must be a positive number > 1");
                                return;
                            }
                            break;
                        case "+/-":
                            try {
                                plusMinusFanout = Integer.parseInt(autogeneratePanel.getFieldText(fieldTitle).trim());
                            }catch(java.lang.NumberFormatException e){
                                ErrorWindow.showErrorWindow("Please give a valid fanout +/- number");
                                return;
                            }
                            
                            //                            System.out.println(plusMinusFanout + " " + fanout/2);
                            if(plusMinusFanout < 0 || (plusMinusFanout >= fanout/2)){
                                ErrorWindow.showErrorWindow("Fanout +/- must be a positive number < fanout/2");
                                return;
                            }
                            break;
                    }
                    
                }
                
                //                System.out.println("name: " + name +"\n" + "nodesType: " + nodesType + "\n" + "start: " + start + "\n" +
                //                        "end: " + end + "\n" + "step: " + step + "\n" + "fanout: " + fanout + "\n" + "plusMinusFanout: " + plusMinusFanout);
                hierarchyPanel1.autogenerateRangeHierarchy(name, nodesType, start, end, step, fanout, plusMinusFanout);
            }
        }
    }//GEN-LAST:event_AutogenerateHierarchyActionPerformed

    private void ExportHierarchyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportHierarchyActionPerformed
        System.out.println("Exporting Hierarchy...");
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(this);
        File dir = null;
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            dir = fc.getSelectedFile();
            //TODO: OPEN FILE HERE
            System.out.println("Export to dir: " + dir.getAbsolutePath());
        } else {
            System.out.println("Open command cancelled by user");
        }
        
        if(dir != null){
            hierarchyPanel1.export(dir.toString());
        }

    }//GEN-LAST:event_ExportHierarchyActionPerformed

    private void ImportHierarchyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportHierarchyActionPerformed
        System.out.println("Importing Hierarchy...");
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);
        File file = null;
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            //TODO: OPEN FILE HERE
            System.out.println("Opening: " + file.getAbsolutePath());
        } else {
            System.out.println("Open command cancelled by user");
        }
        if(file != null){
            hierarchyPanel1.readHierarchy(file.toString());
        }

    }//GEN-LAST:event_ImportHierarchyActionPerformed

    private void SetQuasi_idsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SetQuasi_idsActionPerformed
        if(inputDataPanel1.getData() == null){
            ErrorWindow.showErrorWindow("Please load dataset first");
            return;
        }
        if(hierarchyPanel1.getHierarchies().isEmpty()){
            ErrorWindow.showErrorWindow("Please load hierarchies first");
            return;
        }
        if(inputDataPanel1.getQuasiIdentifiers().isEmpty()){
            ErrorWindow.showErrorWindow("Please first select quasi-identifiers in dataset columns");
            return;
        }
        
        QuasiIdentifiersPanel quasiIdentifiersPanel = new QuasiIdentifiersPanel(inputDataPanel1.getData(), inputDataPanel1.getQuasiIdentifiers(), hierarchyPanel1.getHierarchies());
        
        int result = JOptionPane.showConfirmDialog(null, quasiIdentifiersPanel,
                "Quasi-Identifiers", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            Map<Integer, Hierarchy> quasiToHierarchy = inputDataPanel1.getQuasiIdentifiers();
            Map<Integer, JComboBox> quasiToCombo = quasiIdentifiersPanel.getQuasiToComboBox();
            Map<String, Hierarchy> hierarchies = hierarchyPanel1.getHierarchies();
            
            if(quasiToHierarchy.size() != quasiToCombo.size()){
                ErrorWindow.showErrorWindow("Please select hierarchy for every quasi-identifier.");
                return;
            }
            
            for(Integer i : quasiToCombo.keySet()){
                String hierarchyName = quasiToCombo.get(i).getSelectedItem().toString();
                Hierarchy h = hierarchies.get(hierarchyName);
                quasiToHierarchy.put(i, h);
            }
        }
        
        algorithmsPanel1.setQuasiIdentifiers(inputDataPanel1.getQuasiIdentifiers());
    }//GEN-LAST:event_SetQuasi_idsActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        if(!this.anonymizedDatasetPanel2.isAnonymizedTableRendered()){
            ErrorWindow.showErrorWindow("No anonymized Dataset is loaded");
            return;
        }
        
        System.out.println("Exporting Anonymized Rules...");
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showSaveDialog(this);
        File file = null;
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
        } else {
            System.out.println("Open command cancelled by user");
        }
        
        if(file != null){
            this.anonymizedDatasetPanel2.exportAnonymizationRules(file.getAbsolutePath());
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        if(this.inputDataPanel1.getData() == null){
            ErrorWindow.showErrorWindow("Load dataset first and then import rules");
            return;
        }
        
        System.out.println("Importing Anonymization Rules...");
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);
        File file = null;
        
        //select file
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            System.out.println("Opening: " + file.getAbsolutePath());
        } else {
            System.out.println("Open command cancelled by user");
        }
        
        
        if(file != null){
            AnonymizationRules anonyRules = new AnonymizationRules();
            try {
                if(!anonyRules.importRules(file.getAbsolutePath())){
                    ErrorWindow.showErrorWindow("Wrong file format");
                    return;
                }
                ImportRulesPanel importRulesPanel = new ImportRulesPanel(anonyRules.getAnonymizedRules());
                
                //show panel for rules to be applied
                int result = JOptionPane.showConfirmDialog(null, importRulesPanel,
                        "Import anonymization rules", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    this.anonymizedDatasetPanel2.setDataset(this.inputDataPanel1.getData());
                    this.anonymizedDatasetPanel2.renderInitialTable();
                    this.anonymizedDatasetPanel2.anonymizeWithImportedRules(anonyRules.getAnonymizedRules());
                }
                
            } catch (IOException ex) {
                Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        
        if(!this.anonymizedDatasetPanel2.isAnonymizedTableRendered()){
            ErrorWindow.showErrorWindow("No anonymized Dataset is loaded");
            return;
        }
        
        System.out.println("Exporting Anonymized Dataset...");
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showSaveDialog(this);
        File file = null;
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
        } else {
            System.out.println("Open command cancelled by user");
        }
        
        if(file != null){
            this.anonymizedDatasetPanel2.exportAnonymizedDataset(file.getAbsolutePath());
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
        * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
        */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainGUI().setVisible(true);
            }
        });
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AutogenerateHierarchy;
    private javax.swing.JMenu Data;
    private javax.swing.JMenuItem ExportHierarchy;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenu Hierarchy;
    private javax.swing.JMenuItem ImportData;
    private javax.swing.JMenuItem ImportHierarchy;
    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JPanel ResultsTab;
    private javax.swing.JMenuItem SetQuasi_ids;
    private javax.swing.JPanel SolutionsTab;
    private javax.swing.JTabbedPane Tabs;
    private javax.swing.JPanel TransforamtionTab;
    private privacytool.gui.transformationTab.AlgorithmsPanel algorithmsPanel1;
    private privacytool.gui.resultsTab.AnonymizedDatasetPanel anonymizedDatasetPanel2;
    private privacytool.gui.transformationTab.HierarchyPanel hierarchyPanel1;
    private privacytool.gui.transformationTab.InputDataPanel inputDataPanel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private privacytool.gui.solutionsTab.SolutionsPanel solutionsPanel1;
    // End of variables declaration//GEN-END:variables
}
