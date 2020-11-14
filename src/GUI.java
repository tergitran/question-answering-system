import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JFileChooser;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;


public class GUI extends JFrame {

	/**
	 *
	 */

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtDirect;
	private JTextField txtQuery;

	private String currentPath = "data";
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * Create the frame.
	 */
	public GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1075, 567);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setTitle("He thong hoi dap");
		
		JLabel lblDataTrainDirect = new JLabel("Data directory:");
		lblDataTrainDirect.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		txtDirect = new JTextField();
		txtDirect.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtDirect.setColumns(10);
		
		JButton btnBrowser = new JButton("Browser");
		btnBrowser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()); 
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            // invoke the showsOpenDialog function to show the save dialog 
            int r = fileChooser.showOpenDialog(null); 
            // if the user selects a file 
            if (r == JFileChooser.APPROVE_OPTION) { 
					txtDirect.setText(fileChooser.getSelectedFile().toString());
					currentPath = txtDirect.getText();
            	 }
            else 
            {
            	 System.out.println("No Selection ");
            }
		}
		});
		btnBrowser.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		JLabel lblQaViSystem = new JLabel("QA Vi System");
		lblQaViSystem.setHorizontalAlignment(SwingConstants.CENTER);
		lblQaViSystem.setFont(new Font("Tahoma", Font.PLAIN, 32));
		lblQaViSystem.setForeground(Color.blue);
		
		JLabel lblSearch = new JLabel("Search Querry :");
		lblSearch.setForeground(new Color(0, 0, 0));
		lblSearch.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		txtQuery = new JTextField();
		Font currentFont = txtQuery.getFont(); //getting the current font
		Font font = currentFont.deriveFont((float)20); //deriving a new fontedtText.setFont(font);
		txtQuery.setFont(font);
		txtQuery.setColumns(15);
		JLabel lblAnswer = new JLabel("Answer:");
		lblAnswer.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		JLabel lblResult = new JLabel("");
		lblResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblResult.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		JTextArea txtAResult = new JTextArea();
		JScrollPane sp = new JScrollPane(txtAResult);   // JTextArea is placed in a JScrollPane.
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		
		JButton btnNewButton = new JButton("Search");
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String query = txtQuery.getText();
				QASystem test =new QASystem();
				// String path = txtDirect.getText();
				// String list_key="";
				String result = test.getResult(currentPath,query);
				lblResult.setText(result);
				txtAResult.setText(test.list_key);
				
			}
		});
		
		JButton btnTrainModel = new JButton("Index data");
		btnTrainModel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String path = txtDirect.getText();
				try {
				Index obj = new Index("index");
				obj.index(path);
				JLabel label = new JLabel("Success",JLabel.CENTER);
				JOptionPane.showMessageDialog(null, label, "About", JOptionPane.PLAIN_MESSAGE);

				}
				catch (Exception e)
				{
					e.printStackTrace();
					JLabel label = new JLabel("Fail",JLabel.CENTER);
					JOptionPane.showMessageDialog(null, label, "About", JOptionPane.PLAIN_MESSAGE);

				}
			}
		});
		btnTrainModel.setFont(new Font("Tahoma", Font.BOLD, 14));
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(370)
							.addComponent(lblQaViSystem, GroupLayout.PREFERRED_SIZE, 233, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(53)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(lblSearch, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblAnswer, GroupLayout.PREFERRED_SIZE, 128, GroupLayout.PREFERRED_SIZE))
									.addGap(82)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_contentPane.createSequentialGroup()
											.addComponent(txtQuery, GroupLayout.PREFERRED_SIZE, 392, GroupLayout.PREFERRED_SIZE)
											.addGap(49)
											.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_contentPane.createSequentialGroup()
											.addComponent(lblResult, GroupLayout.PREFERRED_SIZE, 287, GroupLayout.PREFERRED_SIZE)
											.addGap(37)
											.addComponent(sp, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblDataTrainDirect, GroupLayout.PREFERRED_SIZE, 229, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(txtDirect, GroupLayout.PREFERRED_SIZE, 392, GroupLayout.PREFERRED_SIZE)
									.addGap(49)
									.addComponent(btnBrowser, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(btnTrainModel, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)))
							.addGap(32)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(47)
							.addComponent(lblQaViSystem, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
							.addGap(48)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(14)
									.addComponent(txtDirect, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(13)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(btnTrainModel, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
										.addComponent(btnBrowser, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)))))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(127)
							.addComponent(lblDataTrainDirect, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)))
					.addGap(51)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(txtQuery, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblSearch, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
					.addGap(35)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(49)
							.addComponent(lblAnswer, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(35)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(sp, GroupLayout.PREFERRED_SIZE, 172, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblResult, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE))))
					.addGap(13))
		);
		contentPane.setLayout(gl_contentPane);
			
	}
}
