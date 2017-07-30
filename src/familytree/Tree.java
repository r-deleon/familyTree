package familytree;

import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ViewportLimiter;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.FilteredGraphWrapper;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.graph.LayoutUtilities;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.ItemHoverInputMode;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.layout.genealogy.FamilyTreeLayout;
import com.yworks.yfiles.layout.genealogy.FamilyTreeLayoutData;
import com.yworks.yfiles.layout.genealogy.FamilyType;
import com.yworks.yfiles.view.CanExecuteRoutedEventArgs;
import com.yworks.yfiles.view.Command;
import com.yworks.yfiles.view.CommandManager;
import com.yworks.yfiles.view.ExecutedRoutedEventArgs;
import com.yworks.yfiles.view.Pen;
import toolkit.AbstractDemo;

import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import javax.swing.JOptionPane;
import logic.Person;
import logic.Controller;
import logic.Couple;

public class Tree extends AbstractDemo {

    private final HashSet<INode> hiddenNodesSet;

    private FilteredGraphWrapper filteredGraphWrapper;

    private boolean doingLayout;

    private final Controller controller;

    private IMapper<INode, FamilyType> familyTypeMapper;

    public Tree(Controller controller) {
        this.hiddenNodesSet = new HashSet<>();
        this.controller = controller;
    }

    @Override
    protected void configure(JRootPane rootPane) {
        Container contentPane = rootPane.getContentPane();
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(graphComponent, BorderLayout.CENTER);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        JToolBar toolBar = createToolBar();
        configureToolBar(toolBar);
        centerPanel.add(toolBar, BorderLayout.NORTH);
    }

    @Override
    protected void configureToolBar(JToolBar toolBar) {
        super.configureToolBar(toolBar);
        toolBar.add(new JToolBar.Separator());
        toolBar.add(createCommandButtonAction("Create Couple", "couple.png", CREATE_COUPLE, null, graphComponent));
        toolBar.add(createCommandButtonAction("Create Son", "son.png", CREATE_SON, null, graphComponent));
        toolBar.add(createCommandButtonAction("Create Daughter ", "daughter.png", CREATE_DAUGHTER, null, graphComponent));
        toolBar.add(createCommandButtonAction("Create Parents", "family.png", CREATE_PARENTS, null, graphComponent));
    }

    @Override
    public void initialize() {
        // set up defaults for the appearance of the graph elements
        this.registerElementDefaults();

        // disable the default visual representation for focused and highlighted nodes
        this.graphComponent.getSelectionIndicatorManager().setEnabled(false);
        this.graphComponent.getHighlightIndicatorManager().setEnabled(false);
        this.graphComponent.getFocusIndicatorManager().setEnabled(false);

        // wire up the interaction and the input modes for this demo
        this.initializeCommandBindings();
        this.initializeInputMode();
    }

    @Override
    public void onVisible() {
        initializeGraph();
    }

    private void initializeInputMode() {
        GraphViewerInputMode inputMode = new GraphViewerInputMode();
        // disable almost every interaction
        inputMode.setClickableItems(GraphItemTypes.NODE);
        inputMode.setSelectableItems(GraphItemTypes.NONE);
        inputMode.setMarqueeSelectableItems(GraphItemTypes.NONE);
        inputMode.setToolTipItems(GraphItemTypes.NONE);
        inputMode.setPopupMenuItems(GraphItemTypes.NONE);
        // except for focusing of nodes
        inputMode.setFocusableItems(GraphItemTypes.NODE);

        // setup the HoverInputMode and let it trigger a repaint when hovering occurs
        ItemHoverInputMode itemHoverInputMode = inputMode.getItemHoverInputMode();
        itemHoverInputMode.setHoverItems(GraphItemTypes.NODE);
        itemHoverInputMode.addHoveredItemChangedListener((source, args) -> graphComponent.repaint());

        // zoom to the double clicked item
        inputMode.addItemDoubleClickedListener((source, args) -> zoomToCurrentItem());

        this.graphComponent.setInputMode(inputMode);
    }

    private void registerElementDefaults() {
        IGraph graph = this.graphComponent.getGraph();
        graph.getNodeDefaults().setStyle(new LevelOfDetailNodeStyle());
        graph.getNodeDefaults().setSize(new SizeD(100, 100));
        PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
        edgeStyle.setPen(new Pen(new Color(36, 154, 231, 255), 2));
        edgeStyle.setTargetArrow(IArrow.NONE);
        graph.getEdgeDefaults().setStyle(edgeStyle);
    }

    private void initializeCommandBindings() {
        // connect the commands to the methods for the execution and availability of the command
        this.graphComponent.addCommandBinding(CREATE_COUPLE, this::createCouple, this::canExecuteCommand);
        this.graphComponent.addCommandBinding(CREATE_SON, this::createSon, this::canExecuteCommand);
        this.graphComponent.addCommandBinding(CREATE_DAUGHTER, this::createDaughter, this::canExecuteCommand);
        this.graphComponent.addCommandBinding(CREATE_PARENTS, this::createParents, this::canExecuteCommand);
    }

    private void initializeGraph() {
        try {
            // we wrap the graph instance by a filtered graph wrapper
            filteredGraphWrapper = new FilteredGraphWrapper(graphComponent.getGraph(), node -> !this.hiddenNodesSet.contains(node), edge -> true);
            this.graphComponent.setGraph(this.filteredGraphWrapper);

            this.doLayout();

            this.graphComponent.fitGraphBounds();
            this.limitViewport();
        } catch (Exception e) {
        }
    }

    public void doLayout() {
        IGraph graph = graphComponent.getGraph();

        familyTypeMapper = new Mapper<>(); //maps each node to its type   

        Person p = new Person();
        p.setFirstName("Person");
        INode node = graph.createNode();
        familyTypeMapper.setValue(node, FamilyType.FEMALE);
        node.setTag(p);
        controller.addPerson(p);

        FamilyTreeLayoutData familyTreeLayoutData = new FamilyTreeLayoutData();
        familyTreeLayoutData.getFamilyTypes().setMapper(familyTypeMapper);

        FamilyTreeLayout familyTreeLayout = new FamilyTreeLayout();
        familyTreeLayout.setSpacingBetweenFamilyMembers(50);
        familyTreeLayout.setOffsetForFamilyNodes(50);

        LayoutUtilities.applyLayout(graph, familyTreeLayout, familyTreeLayoutData);
    }

    private void limitViewport() {
        this.graphComponent.updateContentRect();
        ViewportLimiter limiter = this.graphComponent.getViewportLimiter();
        limiter.setHonoringBothDimensionsEnabled(false);
        limiter.setBounds(this.graphComponent.getContentRect().getEnlarged(100));
    }

    public void zoomToCurrentItem() {
        if (graphComponent.getCurrentItem() instanceof INode) {
            INode currentItem = (INode) graphComponent.getCurrentItem();
            // visible current item
            if (graphComponent.getGraph().contains(currentItem)) {
                GraphComponent.ZOOM_TO_CURRENT_ITEM_COMMAND.execute(null, graphComponent);
            } else {
                // see if it can be made visible
                IGraph fullGraph = filteredGraphWrapper.getWrappedGraph();
                if (fullGraph.contains(currentItem)) {
                    // hide all nodes except the node to be displayed and all its descendants
                    hiddenNodesSet.clear();
                    fullGraph.getNodes().stream()
                            .filter(testNode -> testNode != currentItem && fullGraph.inDegree(testNode) == 0);

                    // reset the layout to make the animation nicer
                    filteredGraphWrapper.getNodes().forEach(node -> filteredGraphWrapper.setNodeCenter(node, PointD.ORIGIN));
                    filteredGraphWrapper.getEdges().forEach(filteredGraphWrapper::clearBends);
                    refreshLayout();
                }
            }
        }
    }

    public static final Command CREATE_COUPLE = new Command("Create Couple", "Create Couple", Collections.singletonList(KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0)));

    public static final Command CREATE_PARENTS = new Command("Create Parents", "Create Parentss", Collections.singletonList(KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0)));

    public static final Command CREATE_SON = new Command("Create Son", "Create Son", Collections.singletonList(KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0)));

    public static final Command CREATE_DAUGHTER = new Command("Create Daughter", "Create Daughter", Collections.singletonList(KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0)));

    public void canExecuteCommand(Object sender, CanExecuteRoutedEventArgs e) {
        e.setExecutable(true);
        e.setHandled(true);
    }

    private void createCouple(Object sender, ExecutedRoutedEventArgs e) {
        INode currentItem = (INode) graphComponent.getCurrentItem();
        if (currentItem != null && currentItem.getTag() instanceof Person) {
            INode family = graphComponent.getGraph().createNode();
            Couple union = new Couple();
            family.setTag(union);
            familyTypeMapper.setValue(family, FamilyType.FAMILY);
            Person p = (Person) currentItem.getTag();
            graphComponent.getGraph().createEdge(currentItem, family);
            Person newPerson = new Person();
            newPerson.setGenere(1);
            INode node = graphComponent.getGraph().createNode();
            familyTypeMapper.setValue(node, FamilyType.FEMALE);
            node.setTag(newPerson);
            graphComponent.getGraph().createEdge(node, family);
            controller.addPerson(newPerson);
            union.setPerson1(p);
            union.setPerson2(newPerson);
            controller.agregarUnion(union);

        } else {
            JOptionPane.showMessageDialog(null, "Select a person", "Warning", 1);
        }
        refreshLayout();
    }

    private void createSon(Object sender, ExecutedRoutedEventArgs e) {
        INode currentItem = (INode) graphComponent.getCurrentItem();
        if (currentItem != null && currentItem.getTag() instanceof Couple) {
            Couple u = (Couple) currentItem.getTag();
            Person newPerson = new Person();
            newPerson.setGenere(0);
            newPerson.setMyMother(u.getPerson1());
            newPerson.setMyFather(u.getPerson2());
            INode hombre = graphComponent.getGraph().createNode();
            familyTypeMapper.setValue(hombre, FamilyType.MALE);
            hombre.setTag(newPerson);
            graphComponent.getGraph().createEdge(currentItem, hombre);
            controller.addPerson(newPerson);
        } else {
            JOptionPane.showMessageDialog(null, "Select a couple", "Warning", 1);
        }
        refreshLayout();
    }

    private void createDaughter(Object sender, ExecutedRoutedEventArgs e) {
        INode currentItem = (INode) graphComponent.getCurrentItem();
        if (currentItem != null && currentItem.getTag() instanceof Couple) {
            Couple u = (Couple) currentItem.getTag();
            Person newPerson = new Person();
            newPerson.setGenere(1);
            newPerson.setMyMother(u.getPerson1());
            newPerson.setMyFather(u.getPerson2());
            INode node = graphComponent.getGraph().createNode();
            familyTypeMapper.setValue(node, FamilyType.FEMALE);
            node.setTag(newPerson);
            graphComponent.getGraph().createEdge(currentItem, node);
            controller.addPerson(newPerson);
        } else {
            JOptionPane.showMessageDialog(null, "Select a couple", "Warning", 1);
        }
        refreshLayout();
    }

    private void createParents(Object sender, ExecutedRoutedEventArgs e) {
        INode currentItem = (INode) graphComponent.getCurrentItem();
        if (currentItem != null && currentItem.getTag() instanceof Person) {
            Person p = (Person) currentItem.getTag();
            if (p.getMyFather() == null) {
                INode family = graphComponent.getGraph().createNode();
                Couple union = new Couple();
                family.setTag(union);
                familyTypeMapper.setValue(family, FamilyType.FAMILY);

                Person mother = new Person();
                Person father = new Person();
                mother.setGenere(1);
                father.setGenere(0);
                p.setMyMother(mother);
                p.setMyFather(father);
                currentItem.setTag(p);

                INode womenNode = graphComponent.getGraph().createNode();
                familyTypeMapper.setValue(womenNode, FamilyType.FEMALE);
                womenNode.setTag(mother);

                INode manNode = graphComponent.getGraph().createNode();
                familyTypeMapper.setValue(manNode, FamilyType.MALE);
                manNode.setTag(father);

                graphComponent.getGraph().createEdge(womenNode, family);
                graphComponent.getGraph().createEdge(manNode, family);

                graphComponent.getGraph().createEdge(family, currentItem);

                controller.addPerson(father);
                controller.addPerson(mother);
                controller.agregarUnion(union);
            } else {
                JOptionPane.showMessageDialog(null, "Fathers already created", "Warning", 1);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select Person", "Warning", 1);
        }
        refreshLayout();
    }

    private void refreshLayout() {
        CommandManager.invalidateRequerySuggested();

        FamilyTreeLayoutData familyTreeLayoutData = new FamilyTreeLayoutData();
        familyTreeLayoutData.getFamilyTypes().setMapper(familyTypeMapper);

        FamilyTreeLayout familyTreeLayout = new FamilyTreeLayout();
        familyTreeLayout.setSpacingBetweenFamilyMembers(50);
        familyTreeLayout.setOffsetForFamilyNodes(50);

        LayoutExecutor executor = new LayoutExecutor(graphComponent, familyTreeLayout);
        executor.setEasedAnimationEnabled(true);
        executor.setRunningInThread(true);
        executor.setContentRectUpdatingEnabled(true);
        executor.setDuration(Duration.ofMillis(500));
        executor.setLayoutData(familyTreeLayoutData);
        // add hook for cleanup
        executor.addLayoutFinishedListener((source, args) -> {
            doingLayout = false;

            // update viewport limiter to use the new content rect
            graphComponent.getViewportLimiter().setBounds(graphComponent.getContentRect().getEnlarged(20));
        });

        doingLayout = true;
        executor.start();
    }
}
