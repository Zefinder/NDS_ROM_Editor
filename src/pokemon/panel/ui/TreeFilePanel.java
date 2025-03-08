package pokemon.panel.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import pokemon.event.EventListener;
import pokemon.event.EventManager;
import pokemon.event.ui.ArchiveCreatedEvent;
import pokemon.event.ui.ArchiveExtractedEvent;
import pokemon.event.ui.FileDeletedEvent;
import pokemon.event.ui.TreeFileOpened;
import pokemon.panel.ui.popup.DirPopup;

public class TreeFilePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5305053754146010493L;

	private JTree fileTree;
	private Map<Path, MutableTreeNode> nodeMap;

	public TreeFilePanel(File baseFile) throws IOException {
		// White background
		this.setBackground(Color.white);

		nodeMap = new HashMap<Path, MutableTreeNode>();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new NodeObject(baseFile.toPath(), "/"), true);
		Files.walkFileTree(baseFile.toPath(), new NDSFileVisitor(root, nodeMap));

		fileTree = new JTree(root);
		fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		fileTree.addMouseListener(new DoubleClickNode(fileTree));
		this.add(fileTree, BorderLayout.WEST);

		EventManager.getInstance().registerListener(this);
	}

	@EventListener
	public void onArchiveExtracted(ArchiveExtractedEvent event) throws IOException {
		DefaultTreeModel model = (DefaultTreeModel) fileTree.getModel();
		MutableTreeNode node = nodeMap.get(event.getArchivePath());

		// Check if value exist in map (should exist...)
		if (node != null) {
			// Get parent
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
			int index = parentNode.getIndex(node);

			// TODO Remove narc file from map using an event
			nodeMap.remove(event.getArchivePath());

			// Remove narc file from tree
			model.removeNodeFromParent(node);

			// Create new node (extracted archive) and fill it
			DefaultMutableTreeNode extractedArchiveNode = new DefaultMutableTreeNode(
					new NodeObject(event.getExtractedPath(), event.getExtractedPath().getFileName().toString()), true);
			Files.walkFileTree(event.getExtractedPath(), new NDSFileVisitor(extractedArchiveNode, nodeMap));

			// Add to old parent
			model.insertNodeInto(extractedArchiveNode, parentNode, index);

			// Reload model
			model.nodeChanged(parentNode);
		} else {
			// TODO Handle error here
		}
	}

	@EventListener
	public void onArchiveCreated(ArchiveCreatedEvent event) throws IOException {
		DefaultTreeModel model = (DefaultTreeModel) fileTree.getModel();
		MutableTreeNode node = nodeMap.get(event.getArchiveDir());

		// Check if value exist in map (should exist...)
		if (node != null) {
			// Get parent
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
			int index = parentNode.getIndex(node);

			// Remove the archived directory and all its nodes from the map
			Files.walkFileTree(event.getArchiveDir(), new NodeSuppressionVisitor(nodeMap));

			// Remove archived directory from tree
			model.removeNodeFromParent(node);

			// Create new node and add to old parent
			DefaultMutableTreeNode extractedArchiveNode = new DefaultMutableTreeNode(
					new NodeObject(event.getArchivePath(), event.getArchivePath().getFileName().toString()), false);
			model.insertNodeInto(extractedArchiveNode, parentNode, index);

			// Reload model
			model.nodeChanged(parentNode);
		} else {
			// TODO Handle error here
		}
	}

	@EventListener
	public void onFileDeleted(FileDeletedEvent event) throws IOException {
		// Get node from model and verify if it exists
		Path fileDeletedPath = event.getDeletedFile().toPath();
		DefaultTreeModel model = (DefaultTreeModel) fileTree.getModel();
		MutableTreeNode node = nodeMap.get(fileDeletedPath);

		if (node != null) {
			// Get parent
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
			
			// Remove the file and all its nodes (if directory) from the map
			Files.walkFileTree(fileDeletedPath, new NodeSuppressionVisitor(nodeMap));

			// Remove archived directory from tree
			model.removeNodeFromParent(node);
			
			// Reload model
			model.nodeChanged(parentNode);
		} else {
			// TODO Handle error here
		}
	}

	private static class NodeObject {

		private Path nodePath;
		private String nodeName;

		public NodeObject(Path nodePath, String nodeName) {
			this.nodePath = nodePath;
			this.nodeName = nodeName;
		}

		public Path getNodePath() {
			return nodePath;
		}

		public String getNodeName() {
			return nodeName;
		}

		@Override
		public String toString() {
			return nodeName;
		}

		// Only the path is unique
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof NodeObject) {
				return ((NodeObject) obj).getNodePath().equals(this.nodePath);
			}

			return false;
		}

		@Override
		public int hashCode() {
			return nodePath.hashCode();
		}
	}

	private static class NDSFileVisitor extends SimpleFileVisitor<Path> {

		private Map<Path, MutableTreeNode> nodeMap;
		private DefaultMutableTreeNode rootNode;

		public NDSFileVisitor(DefaultMutableTreeNode rootNode, Map<Path, MutableTreeNode> nodeMap) {
			this.rootNode = rootNode;
			this.nodeMap = nodeMap;
			nodeMap.put(((NodeObject) (rootNode.getUserObject())).getNodePath(), rootNode);
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			super.visitFile(file, attrs);

			// Create file node
			NodeObject leaf = new NodeObject(file, file.getFileName().toString());
			DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(leaf, false);

			// Add to tree and to map
			rootNode.add(fileNode);
			nodeMap.put(file, fileNode);

			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			super.preVisitDirectory(dir, attrs);

			// Create a new node that will allow children and create a new visitor
			NodeObject subtree = new NodeObject(dir, dir.getFileName().toString());
			DefaultMutableTreeNode subtreeNode = new DefaultMutableTreeNode(subtree, true);

			// If it is the root node, we continue
			if (((NodeObject) (rootNode.getUserObject())).getNodePath().equals(subtree.getNodePath())) {
				return FileVisitResult.CONTINUE;
			}

			// Create visitor for this node, automatically added to map
			Files.walkFileTree(dir.toAbsolutePath(), new NDSFileVisitor(subtreeNode, nodeMap));
			rootNode.add(subtreeNode);

			// Do not go into the subtree, already done with another visitor
			return FileVisitResult.SKIP_SUBTREE;
		}
	}

	private static class NodeSuppressionVisitor extends SimpleFileVisitor<Path> {

		private Map<Path, MutableTreeNode> nodeMap;

		public NodeSuppressionVisitor(Map<Path, MutableTreeNode> nodeMap) {
			this.nodeMap = nodeMap;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			super.visitFile(file, attrs);

			nodeMap.remove(file);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			super.preVisitDirectory(dir, attrs);

			nodeMap.remove(dir);
			return FileVisitResult.CONTINUE;
		}
	}

	private static class DoubleClickNode extends MouseAdapter {

		private JTree fileTree;

		public DoubleClickNode(JTree fileTree) {
			this.fileTree = fileTree;
		}

		public void mousePressed(MouseEvent e) {
			int selRow = fileTree.getRowForLocation(e.getX(), e.getY());
			TreePath selPath = fileTree.getPathForLocation(e.getX(), e.getY());

			if (selRow != -1) {
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
				NodeObject node = (NodeObject) treeNode.getUserObject();

				// Check right click menu
				if (SwingUtilities.isRightMouseButton(e)) {
					// Select clicked row
					fileTree.setSelectionRow(selRow);

					// Show the good popup menu
					if (!treeNode.isLeaf()) {
						DirPopup popup = new DirPopup(node.getNodePath());
						popup.show(e.getComponent(), e.getX(), e.getY());
					} else {
//						System.out.println(node.getNodeName());
					}
				} else {
					if (treeNode.isLeaf() && e.getClickCount() == 2) {
						TreeFileOpened event = new TreeFileOpened(node.getNodePath(), node.getNodeName());
						EventManager.getInstance().throwEvent(event);
					}
				}
			}
		}
	}
}
