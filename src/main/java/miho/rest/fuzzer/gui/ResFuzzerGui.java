package miho.rest.fuzzer.gui;

import kg.apc.jmeter.gui.BrowseAction;
import miho.rest.fuzzer.RestFuzzerSampler;
import miho.rest.fuzzer.utility.RestFuzzerUtil;
import miho.rest.fuzzer.parser.DependencyHandler;
import miho.rest.fuzzer.fuzzer.FuzzHandler;
import miho.rest.fuzzer.parser.GrammarHandler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;

public class ResFuzzerGui extends AbstractSamplerGui {
    private static final Logger LOG = LoggerFactory.getLogger(ResFuzzerGui.class);
    
    private final JTextField baseUrl = new JTextField("");
//    private final JTextField specFilePathField = new JTextField("");
    private final JTextField grammarFilePathField = new JTextField("");
    private final JTextField dependencyFilePathField = new JTextField("");
    private final JTextField fuzzingTimeField = new JTextField("50");
    private final JTextField coverageField = new JTextField();
    
//    JButton specFileBrowseBtn = new JButton("Browse");
    JButton grammarFileBrowseBtn = new JButton("Browse");
    JButton dependencyFileBrowseBtn = new JButton("Browse");
    JButton startFuzzBtn = new JButton("Start Fuzz");
    
    private final GrammarHandler grammarHandler = GrammarHandler.getInstance();
    private final DependencyHandler dependencyHandler = DependencyHandler.getInstance();
    
    private final JTextField responseCode = new JTextField();
    
    public ResFuzzerGui() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        add(createDummySamplerPanel(), BorderLayout.CENTER);
    }
    
    private JPanel createDummySamplerPanel() {
        JPanel dummySamplerPanel = new JPanel();
        dummySamplerPanel.setBorder(BorderFactory.createTitledBorder("Config"));
        GroupLayout layout = new GroupLayout(dummySamplerPanel);
        dummySamplerPanel.setLayout(layout);
        
//        specFileBrowseBtn.addActionListener(new BrowseAction(specFilePathField, false));
//        specFilePathField.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                if (!specFilePathField.getText().equals("") && RestFuzzerUtil.isSpecFileValid(specFilePathField.getText())) {
//                    String extension = FilenameUtils.getExtension(specFilePathField.getText());
//                    RestFuzzerEnum.SpecType specType = RestFuzzerEnum.SpecType.getTypeFromExtension(extension);
//                    try {
//                        specFileHandler.compile(specFilePathField.getText(), specType);
//                    } catch (IOException | InterruptedException ex) {
//                        ex.printStackTrace();
//                    }
//                } else {
//                    System.out.println("SPECIFICATION FILE INVALID!");
//                }
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//            }
//        });
        
        grammarFileBrowseBtn.addActionListener(new BrowseAction(grammarFilePathField, false));
        grammarFilePathField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!grammarFilePathField.getText().equals("") && RestFuzzerUtil.isSpecFileValid(grammarFilePathField.getText())) {
                    try {
                        grammarHandler.getGrammarDict(grammarFilePathField.getText());
                    } catch (IOException | JSONException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("READ GRAMMAR FILE ERROR!");
                }
            }
    
            @Override
            public void removeUpdate(DocumentEvent e) {
            }
    
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        
        dependencyFileBrowseBtn.addActionListener(new BrowseAction(dependencyFilePathField, false));
        dependencyFilePathField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!dependencyFilePathField.getText().equals("") && RestFuzzerUtil.isSpecFileValid(dependencyFilePathField.getText())) {
                    try {
                        dependencyHandler.getDependency(dependencyFilePathField.getText());
                    } catch (IOException | JSONException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("READ DEPENDENCY FILE ERROR!");
                }
            }
    
            @Override
            public void removeUpdate(DocumentEvent e) {
            }
    
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        
        startFuzzBtn.addActionListener(e -> {
            if (!baseUrl.getText().isBlank() &&
                (baseUrl.getText().contains("http") || baseUrl.getText().contains("https")) &&
                !grammarFilePathField.getText().isBlank() && !dependencyFilePathField.getText().isBlank()
            ) {
                FuzzHandler fuzzHandler = FuzzHandler.getInstance();
                fuzzHandler.prepareFuzzing(baseUrl.getText(), fuzzingTimeField.getText());
            } else {
                System.out.println("URL OR FILE IS INVALID!");
            }
        });
        
        populateUIElement(layout);
        return dummySamplerPanel;
    }
    
    private void populateUIElement(GroupLayout layout) {
        JLabel urlLabel = new JLabel("Base URL");
//        JLabel specFilePathLabel = new JLabel("Spec File Path");
        JLabel grammarFilePathLabel = new JLabel("Grammar File Path");
        JLabel dependencyFilePathLabel = new JLabel("Dependency File Path");
        JLabel fuzzingTimeLabel = new JLabel("Fuzzing Time (sec)");
        JLabel coverageLabel = new JLabel("Coverage");
        
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addComponent(urlLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(baseUrl)
                )
//                .addGroup(
//                    layout.createSequentialGroup()
//                        .addComponent(specFilePathLabel)
//                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
//                        .addComponent(specFilePathField)
//                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
//                        .addComponent(specFileBrowseBtn)
//                )
                .addGroup(
                    layout.createSequentialGroup()
                        .addComponent(grammarFilePathLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(grammarFilePathField)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(grammarFileBrowseBtn)
                )
                .addGroup(
                    layout.createSequentialGroup()
                        .addComponent(dependencyFilePathLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dependencyFilePathField)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dependencyFileBrowseBtn)
                )
//                .addGroup(
//                    layout.createSequentialGroup()
//                        .addComponent(fuzzingTimeLabel)
//                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
//                        .addComponent(fuzzingTimeField)
//                )
//                .addGroup(
//                    layout.createSequentialGroup()
//                        .addComponent(coverageLabel)
//                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
//                        .addComponent(coverageField)
//                )
                .addGroup(
                    layout.createSequentialGroup()
                        .addComponent(startFuzzBtn)
                )
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(urlLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE)
                        .addComponent(baseUrl, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE)).addGap(5)
//                .addGroup(
//                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
//                        .addComponent(specFilePathLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
//                            GroupLayout.PREFERRED_SIZE)
//                        .addComponent(specFilePathField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
//                            GroupLayout.PREFERRED_SIZE)
//                        .addComponent(specFileBrowseBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
//                            GroupLayout.PREFERRED_SIZE)).addGap(5)
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(grammarFilePathLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE)
                        .addComponent(grammarFilePathField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE)
                        .addComponent(grammarFileBrowseBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE)).addGap(5)
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(dependencyFilePathLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE)
                        .addComponent(dependencyFilePathField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE)
                        .addComponent(dependencyFileBrowseBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE)).addGap(5)
//                .addGroup(
//                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
//                        .addComponent(fuzzingTimeLabel, GroupLayout.PREFERRED_SIZE,
//                            GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
//                        .addComponent(fuzzingTimeField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
//                            GroupLayout.PREFERRED_SIZE)).addGap(5)
//                .addGroup(
//                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
//                        .addComponent(coverageLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
//                            GroupLayout.PREFERRED_SIZE)
//                        .addComponent(coverageField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
//                            GroupLayout.PREFERRED_SIZE)).addGap(5)
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(startFuzzBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE)).addGap(5)
        );
    }
    
    @Override
    public String getLabelResource() {
        return "Rest Fuzzer";
    }
    
    @Override
    public String getStaticLabel() {
        return getLabelResource();
    }
    
    @Override
    public TestElement createTestElement() {
        RestFuzzerSampler restFuzzerSampler = new RestFuzzerSampler();
        configureTestElement(restFuzzerSampler);
        return restFuzzerSampler;
    }
    
    @Override
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
        if (element instanceof RestFuzzerSampler) {
            RestFuzzerSampler restFuzzerSampler = (RestFuzzerSampler) element;
            restFuzzerSampler.setResponseCode(responseCode.getText());
        }
    }
    
    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof RestFuzzerSampler) {
            RestFuzzerSampler dummySampler = (RestFuzzerSampler) element;
            coverageField.setText(dummySampler.getResponseCode());
            responseCode.setText(dummySampler.getResponseCode());
        }
    }
    
    @Override
    public void clearGui() {
        super.clearGui();
        baseUrl.setText("");
        fuzzingTimeField.setText("50");
        coverageField.setText("");
//        specFilePathField.setText("");
        grammarFilePathField.setText("");
        dependencyFilePathField.setText("");
        
        coverageField.setText("");
        responseCode.setText("");
    }
}
