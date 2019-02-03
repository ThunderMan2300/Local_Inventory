import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.util.*;
import java.io.*;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;

public class WorkshopInventory {

	private JFrame frame;
	private JFrame fake_frame;
	private JPanel mother;

	private final String START = "START";
	private final String USER = "USER";
	private final String ADMIN = "ADMIN";
	private String adminPass;
	private String name = "";
	private boolean create = true;

	private ArrayList<String[]> disposableItems;
	private ArrayList<String[]> nondisposableItems;
	private ArrayList<String[]> userNames;
	private ArrayList<String[]> displaying;
	private ArrayList<String[]> displaying_admin;
	private ArrayList<String> loginRecord;

	private JPasswordField passwordField;
	private JTable table;
	private JTable table_1;
	private JTextField txtChangeAdminPassword;
	private JTextField newAdminPassField;
	private JTextField txtWelcomeAdministrator;
	private JPasswordField currentPassEnter;
	private JPasswordField newPassReenter;
	private JPasswordField newPassEnter;
	private JTextField textField;
	private JTable loginRecordTable;
	private DefaultTableModel model1;
	private DefaultTableModel modelAdmin;
	private JTable user_table;
	private JTable admin_table;
	private JTextField searchTxt;
	private JTextField searchTxt_admin;
	private JSpinner spinner;
	private DefaultTableModel filteredItems;
	private JComboBox comboBox_1;
	private JPanel createDelete;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WorkshopInventory window = new WorkshopInventory();
					window.fake_frame.setVisible(true);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WorkshopInventory() {
		//adminPass = "admin";
		disposableItems = new ArrayList<String[]>();
		nondisposableItems = new ArrayList<String[]>();
		loginRecord = new ArrayList<String>();
		userNames = new ArrayList<String[]>();
		displaying = new ArrayList<String[]>();

		readInData(); //Reads from files
		initialize(); //Sets the windows
	}

	/*
	 *	Populates table with disposible items
	 */
	private void tableItems_disposableItems( TableModel model1 )
	{
		for(int a = 0; a < disposableItems.size(); a++)
		{
			model1.setValueAt(disposableItems.get(a)[0], a, 0);
			model1.setValueAt(disposableItems.get(a)[1], a, 1);
		}
	}

	private void tableItems_allItems(TableModel modelAdmin)
	{
		for(int a = 0; a < disposableItems.size(); a++)
		{
			modelAdmin.setValueAt(disposableItems.get(a)[0], a, 0);
			modelAdmin.setValueAt(disposableItems.get(a)[1], a, 1);
		}
		for(int a = 0; a < nondisposableItems.size(); a++)
		{
			modelAdmin.setValueAt(nondisposableItems.get(a)[0], a + disposableItems.size(), 0);
			modelAdmin.setValueAt(nondisposableItems.get(a)[1], a + disposableItems.size(), 1);
		}
	}

	/*
	 *	Search method in charge of displaying only what mathces the searchTerm
	 */
	private void searchFilter(String searchTerm)
	{
		displaying = new ArrayList<String[]>();
		filteredItems = new DefaultTableModel(disposableItems.size(), 2);
		searchTerm = searchTerm.toLowerCase();

		int it_num = 0;
		for(String[] x : disposableItems)
		{
			String temp = x[0].toLowerCase();
			if(temp.indexOf(searchTerm) > -1)
			{
				filteredItems.setValueAt(x[0], it_num, 0);
				filteredItems.setValueAt(x[1], it_num, 1);
				String[] a = {x[0], x[1]};
				displaying.add(a);
				it_num++;
			}
		}
		user_table.setModel(filteredItems);
	}

	private void searchFilterAdmin(String searchTerm)
	{
		displaying_admin = new ArrayList<String[]>();
		filteredItems = new DefaultTableModel(disposableItems.size() + nondisposableItems.size(), 2);
		searchTerm = searchTerm.toLowerCase();

		int it_num = 0;
		for(String[] x : disposableItems)
		{
			String temp = x[0].toLowerCase();
			if(temp.indexOf(searchTerm) > -1)
			{
				filteredItems.setValueAt(x[0], it_num, 0);
				filteredItems.setValueAt(x[1], it_num, 1);
				String[] a = {x[0], x[1]};
				displaying_admin.add(a);
				it_num++;
			}
		}
		for(String[] x : nondisposableItems)
		{
			String temp = x[0].toLowerCase();
			if(temp.indexOf(searchTerm) > -1)
			{
				filteredItems.setValueAt(x[0], it_num, 0);
				filteredItems.setValueAt(x[1], it_num, 1);
				String[] a = {x[0], x[1]};
				displaying_admin.add(a);
				it_num++;
			}
		}
		admin_table.setModel(filteredItems);
	}

	/*
	 *	Event that checks if key is pressed and released for continously calling search method
	 */
	private void searchTxtKeyReleased(java.awt.event.KeyEvent evt) {
        searchFilter(searchTxt.getText());
    }

	private void searchTxtKeyReleased_admin(java.awt.event.KeyEvent evt) {
        searchFilterAdmin(searchTxt_admin.getText());
    }

	/*
	 *	Check if value is good to be removed and remove if amount is right
	 */
    private void remove()
    {
    	if(user_table.getSelectionModel().isSelectionEmpty()) //If item is not selected
    	{
    		JOptionPane.showMessageDialog(frame,
		    "Select an item",
		    "NULL ITEM",
		    JOptionPane.WARNING_MESSAGE);
    	}
    	else if((int)spinner.getValue() <= 0) //If spinner is less than 0
    	{
    		JOptionPane.showMessageDialog(frame,
		    "Select a number greater than 0",
		    "ZERO EXCEPTION",
		    JOptionPane.WARNING_MESSAGE);
    	}
    	else //First check point to remove value
    	{
			int n = JOptionPane.showConfirmDialog(
		    frame,
		    "Are you sure you want to submit?",
		    "CONFIRM",
		    JOptionPane.YES_NO_OPTION);
		    //0 = Yes
		    //1 = No

		    if(n == 0) //If user answered yes
		    {
		    	int amount_selected = (int)spinner.getValue();
		    	int amount_av = 0;
		    	int sel = user_table.getSelectedRow();
		    	String item_selected = null;
		    	boolean readingFromDis = false;

		    	/*
		    	 *	I totally forgot what this try catch part of the
		    	 *	code is for, but if it works don't change it...
		    	 *	Btw for some reason the program needs it...
		    	 */
		    	try
		    	{
		    		item_selected = displaying.get(sel)[0];
		    		amount_av = Integer.parseInt(displaying.get(sel)[1]);
		    		readingFromDis = true;
		    	}
		    	catch (IndexOutOfBoundsException e)
		    	{
		    		item_selected = disposableItems.get(sel)[0];
		    		amount_av = Integer.parseInt(disposableItems.get(sel)[1]);
		    		readingFromDis = false;
		    	}

		    	if(amount_selected > amount_av) //User tries to remove more than available
		    	{
			    	JOptionPane.showMessageDialog(frame,
				    "You are taking more items than on stock",
				    "OVERFLOW",
				    JOptionPane.ERROR_MESSAGE);
		    	}
		    	else
		    	{
		    		if(readingFromDis)
		    			for(int a = 0; a < disposableItems.size(); a++)
		    				if(disposableItems.get(a)[0].equals(item_selected))
		    					sel = a;
		    		disposableItems.get(sel)[1] = amount_av - amount_selected + "";
		    		int tempRem = amount_av - amount_selected;
		    		searchTxt.setText("");
		    		searchFilter("");
		    		String str = "\t-- " + item_selected + "\t\t" + amount_selected + "\t\t\t Amount remaining: " + tempRem;
		    		recordData(str);
		    		updateRecordData();
		    	}
		    }
		    else
		    {
		    	//User hit cancel
		    }
    	}
    }

    private void recordData(String str)
    {
    	try
		{
			Scanner login = new Scanner(new File("LoginList.dat"));
			String all = "";
			while(login.hasNext())
			{
				all += login.nextLine() + "\n";
			}
			PrintWriter printWriter = new PrintWriter("LoginList.dat");
			printWriter.println(str);
			printWriter.println(all);
			printWriter.close();
		}
		catch(IOException e)
		{System.out.println("ERROR");}
    }


	/*
	 *	Reads from files filling disposableItems, nonDisposableItems,
	 *	loginList, and userNames list with saved data.
	 **/
	private void readInData()
	{
		try
		{
		//	File toRead = new File("InventoryItems.dat");
		//	System.out.println(toRead.getAbsolutePath());
			Scanner scan1 = new Scanner(new File("DisposableItems.dat"));

			while ( scan1.hasNext() )
			{
				String item = scan1.next();
				item.replaceAll("_", " ");
				String num = scan1.next();
				String min = scan1.next();
				String[] entry = {item, num, min};
				disposableItems.add(entry);
			}

			Scanner scan2 = new Scanner(new File("NondisposableItems.dat"));

			while ( scan2.hasNext() )
			{
				String item = scan2.next();
				item.replaceAll("_", " ");
				String num = scan2.next();
				String[] entry = {item, num};
				nondisposableItems.add(entry);
			}

			Scanner scan3 = new Scanner(new File("LoginList.dat"));

			while ( scan3.hasNext() )
			{
				String name = scan3.nextLine();
				loginRecord.add(name);
			}

			Scanner scan4 = new Scanner(new File("UserNames.dat"));

			while ( scan4.hasNext() )
			{
				String us = scan4.next();
				String ps = scan4.next();

				if(us.equals("admin"))
				{
					adminPass = ps;
				}

				String[] login = {us, ps};
				userNames.add(login);
			}
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File not found");
		}

	}

	private void updateLoginRecord()
	{
		Object[][] enter = new Object[loginRecord.size()][2];
		for ( int i = 0; i < enter.length; i++ )
		{
			int semiIndex = loginRecord.get(i).indexOf(";");
			String idNum = loginRecord.get(i).substring(0,semiIndex);
			String time = loginRecord.get(i).substring(semiIndex+1);
			enter[i][0] = idNum;
			enter[i][1] = time;
		}
		loginRecordTable.setModel(new DefaultTableModel(
				enter,
				new String[] {
					"ID", "Time and Date"
				}
			));
	}

	private void updateRecordData()
	{
		try
		{
			PrintWriter printWriter = new PrintWriter("DisposableItems.dat");
			for(String[] a : disposableItems)
			{
				printWriter.println(a[0] + " " + a[1] + " " + a[2]);
			}
			printWriter.close();
		}
		catch(IOException e)
		{System.out.println("ERROR");}
	}


	/*
	 *	Ask for User input of a name and calls method to store it
	 */
	public String produceNickname(String id)
	{
		String name = (String)JOptionPane.showInputDialog("Enter your Name:");

		String[] temp = {id, name};
		userNames.add(temp);

		// Save name
		saveName();
		return name;
	}

	public void saveName()
	{
		try{
			PrintWriter save = new PrintWriter(new File("userNames.dat")); //Saves previously inputted name into file
			for(String[] y : userNames)
			{
				save.println(y[0] + " " + y[1]);
			}
			save.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File not found");
		}
	}

	private  void saveName(String pass)
	{
		try{
			PrintWriter save = new PrintWriter(new File("userNames.dat")); //Saves previously inputted name into file
			for(String[] y : userNames)
			{
				if(y[0].equals("admin"))
					save.println(y[0] + " " + pass);
				else
					save.println(y[0] + " " + y[1]);
			}
			save.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File not found");
		}
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		fake_frame = new JFrame("F120 Workshop Inventory");
		fake_frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		fake_frame.setUndecorated(true);
		fake_frame.setDefaultCloseOperation(0);

		frame = new JFrame("F120 Workshop Inventory");
		frame.setBounds(100, 100, 450, 300);
		frame.setUndecorated(true); //Hide min/max/close button
		frame.setDefaultCloseOperation(0); //Makes window unclosable
		frame.setAlwaysOnTop(true);

		mother = new JPanel();
		frame.getContentPane().add(mother, BorderLayout.CENTER);
		mother.setLayout(new CardLayout(0, 0));

		/****start screen initialization****
		 */

		/*
		 *	First Screen where asks for ID
		 */
		JPanel startScreen = new JPanel();
		mother.add(startScreen, START);
		startScreen.setBounds(100, 100, 450, 300);
		startScreen.setLayout(null);

		JLabel lblEnterId = new JLabel("Enter ID");
		lblEnterId.setHorizontalAlignment(SwingConstants.CENTER);
		lblEnterId.setBounds(startScreen.getWidth()/2-61, startScreen.getHeight()/2-50, 122, 14);
		startScreen.add(lblEnterId);

		passwordField = new JPasswordField();
		passwordField.setColumns(10);
		passwordField.setBounds(startScreen.getWidth()/2-50, startScreen.getHeight()/2-30, 100, 20);
		startScreen.add(passwordField);

		JButton btnEnter = new JButton("Enter");
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String id = new String(passwordField.getPassword());//Contains the password or ID number
				String fakeCheck = id.replaceAll("[^0-9]", "A");//Replaces all non letters with A
				if ( id.equals(adminPass)) //Checks if password matches admin
				{
					lblEnterId.setText("Enter ID");
					passwordField.setText("");
					CardLayout cl = (CardLayout)(mother.getLayout());
					cl.show(mother, ADMIN); //Changes to ADMIN screen
				}
				else if ( id.equals("") || fakeCheck.indexOf("A")> -1 || id.length() < 6 ) //Checks is password/id is invalid
				{
					lblEnterId.setText("Invalid ID");
				}
				else //If ID is valid, continue:
				{
					lblEnterId.setText("Enter ID");
					passwordField.setText("");

					boolean comingBack = false;
					for(String[] a : userNames) //Checks username list to see if ID has previously logged in
					{
						if(a[0].equals(id))
						{
							comingBack = true;
							name = a[1];
						}
					}
					if(!comingBack) //New user: asked to enter nickname
						name = produceNickname(id);

					/*
					 *	Login registry code, gather time, date and name in order to store in login file
					 */
					LocalDateTime time = LocalDateTime.now();
					String loginDate = time.getMonthValue() + "/" + time.getDayOfMonth() + "/" + time.getYear();
					String loginTime = time.getHour() + ":";
					if ( time.getMinute() < 10 )
					{
						loginTime += "0" + time.getMinute();
					}
					else
					{
						loginTime += time.getMinute();
					}
					String loginLog = name + ", " + id + ";" + loginDate + " @ " + loginTime; //null, 000000;1/6/2019 @ 21:16
					//System.out.println(loginLog); Prints log in info
					loginRecord.add(loginLog);

					CardLayout cl = (CardLayout)(mother.getLayout());
					cl.show(mother, USER);
				}
			}
		});

		btnEnter.setBounds(startScreen.getWidth()/2-46, startScreen.getHeight()/2, 92, 23);
		startScreen.getRootPane().setDefaultButton(btnEnter);
		startScreen.add(btnEnter);

		JLabel lblWorkshopInventory = new JLabel("Workshop Inventory");
		lblWorkshopInventory.setHorizontalAlignment(SwingConstants.CENTER);
		lblWorkshopInventory.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblWorkshopInventory.setBounds(startScreen.getWidth()/2-72, startScreen.getHeight()/2 - 100, 144, 34);
		startScreen.add(lblWorkshopInventory);
		startScreen.setBounds(100, 100, 450, 300);


		/*
		 *	****user screen initialization****
		 */
		JTabbedPane userScreen1 = new JTabbedPane(JTabbedPane.TOP);
		mother.add(userScreen1, USER);

		JPanel userLogout = new JPanel();
		userScreen1.addTab("Logout", null, userLogout, null);
		userLogout.setLayout(null);

		JLabel lblWelcomeUser = new JLabel("Welcome User");
		lblWelcomeUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcomeUser.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblWelcomeUser.setBounds(144, 70, 124, 14);
		userLogout.add(lblWelcomeUser);

		JButton btnLogout = new JButton("Logout"); //Logout button
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CardLayout cl = (CardLayout)(mother.getLayout());
				cl.show(mother, START);
				recordData(loginRecord.get(loginRecord.size()-1)); //Save login data once logged out
			}
		});
		btnLogout.setBounds(165, 105, 89, 23);
		userLogout.add(btnLogout);

		JPanel removeItems = new JPanel();
		userScreen1.addTab("Remove Items", null, removeItems, null);
		removeItems.setLayout(null);

		JList list = new JList();
		list.setBounds(284, 16, 0, 0);

		model1 = new DefaultTableModel(disposableItems.size(), 2);
		tableItems_disposableItems(model1);
		user_table = new JTable(model1){
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};

		user_table.setBounds(251, 30, 170, 172);
		user_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scroll_table = new JScrollPane(user_table);	// add table to scroll panel
	    scroll_table.setBounds(251, 30, 168, 180);
	    scroll_table.setVisible(true);
		user_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		searchTxt = new JTextField();
		searchTxt.setFont(new java.awt.Font("Tahoma", 0, 12)); // SEARCH FIELD
		searchTxt.setBounds(20, 20, 210, 25);
        searchTxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchTxtKeyReleased(evt);
            }
        });

		removeItems.add(scroll_table);
		removeItems.add(list);
		removeItems.add(searchTxt);

		SpinnerModel jsM = new SpinnerNumberModel(2, 1, 150, 1);
		spinner = new JSpinner(jsM);
		spinner.setBounds(125, 90, 60, 25);
		removeItems.add(spinner);
		JButton btnRemove = new JButton("Remove");	//Remove button
		btnRemove.setBounds(30, 90, 90, 25);
		removeItems.add(btnRemove);
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remove();
			}
		});

		JPanel checkOut = new JPanel();
		userScreen1.addTab("Check Out", null, checkOut, null);
		checkOut.setLayout(null);


		/****admin screen initialization****
		 */
		loginRecordTable = new JTable();
		loginRecordTable.setEnabled(false);
		loginRecordTable.setColumnSelectionAllowed(false);
		loginRecordTable.setRowSelectionAllowed(false);
		loginRecordTable.setBounds(68, 52, 309, 127);

		JTabbedPane adminScreen1 = new JTabbedPane(JTabbedPane.TOP);
		adminScreen1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if ( adminScreen1.getSelectedIndex() == 4 )
				{
					updateLoginRecord();
				}
			}
		});
		mother.add(adminScreen1, ADMIN);

		JPanel welcomeScreen = new JPanel();
		adminScreen1.addTab("Welcome", null, welcomeScreen, null);
		welcomeScreen.setLayout(null);

		JButton btnLogout_2 = new JButton("Logout");
		btnLogout_2.setBounds(161, 130, 84, 23);
		welcomeScreen.add(btnLogout_2);
		btnLogout_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout)(mother.getLayout());
				cl.show(mother, START);
			}
		});
		JButton exitScreen = new JButton("Exit Application"); //Button to terminate Application
		exitScreen.setBounds(135, 160, 140, 23);
		welcomeScreen.add(exitScreen);
		exitScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		txtWelcomeAdministrator = new JTextField();
		txtWelcomeAdministrator.setHorizontalAlignment(SwingConstants.CENTER);
		txtWelcomeAdministrator.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txtWelcomeAdministrator.setText("Welcome, Administrator");
		txtWelcomeAdministrator.setBounds(111, 52, 185, 46);
		txtWelcomeAdministrator.setEditable(false);
		welcomeScreen.add(txtWelcomeAdministrator);
		txtWelcomeAdministrator.setColumns(10);

		JPanel addRemove = new JPanel();
		adminScreen1.addTab("Add/Remove", null, addRemove, null);
		addRemove.setLayout(null);

		createDelete = new JPanel();
		adminScreen1.addTab("Create/Delete", null, createDelete, null);
		createDelete.setLayout(null);

		comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"Create", "Delete"}));
		comboBox_1.setBounds(50, 25, 75, 20);
		createDelete.add(comboBox_1);

		create_delete_window(createDelete); //Adds all the labels and textFields to create or delete item

		comboBox_1.addActionListener (new ActionListener () { //Event Listener for Create/Delete Box
    	public void actionPerformed(ActionEvent e) {
    		//System.out.println(comboBox_1.getSelectedItem().toString().equals("Create"));
        		if(comboBox_1.getSelectedItem().toString().equals("Create"))
        			create = true;
        		else
        			create = false;

        		createDelete.removeAll();
        		createDelete.revalidate();
        		createDelete.repaint();
        		create_delete_window(createDelete);
        		createDelete.add(comboBox_1);
    		}
		});

		JPanel changeID = new JPanel();
		adminScreen1.addTab("Change ID", null, changeID, null);
		changeID.setLayout(null);

		currentPassEnter = new JPasswordField();
		currentPassEnter.setBounds(63, 76, 102, 20);
		changeID.add(currentPassEnter);

		newPassReenter = new JPasswordField();
		newPassReenter.setBounds(238, 111, 102, 20);
		changeID.add(newPassReenter);

		newPassEnter = new JPasswordField();
		newPassEnter.setBounds(238, 52, 102, 20);
		changeID.add(newPassEnter);

		JLabel lblEnterCurrentId = new JLabel("Enter current ID");
		lblEnterCurrentId.setBounds(63, 55, 102, 14);
		changeID.add(lblEnterCurrentId);

		JLabel lblEnterNewId = new JLabel("Enter new ID");
		lblEnterNewId.setBounds(238, 30, 102, 14);
		changeID.add(lblEnterNewId);

		JLabel lblReenterId = new JLabel("Reenter new ID");
		lblReenterId.setBounds(238, 87, 102, 14);
		changeID.add(lblReenterId);

		JLabel lblMessage = new JLabel("hi");
		lblMessage.setVisible(false);
		lblMessage.setBounds(63, 142, 148, 23);
		changeID.add(lblMessage);

		JButton updateID = new JButton("Submit");
		updateID.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String oldPass = new String(currentPassEnter.getPassword());
				String newPass1 = new String(newPassEnter.getPassword());
				String newPass2 = new String(newPassReenter.getPassword());
				if ( !oldPass.equals(adminPass) )
				{
					lblMessage.setVisible(true);
					lblMessage.setText("Can't use old ID");

					currentPassEnter.setText("");
					newPassEnter.setText("");
					newPassReenter.setText("");
				}
				else if ( oldPass.equals(newPass1) )
				{
					lblMessage.setVisible(true);
					lblMessage.setText("Can't use old ID");

					currentPassEnter.setText("");
					newPassEnter.setText("");
					newPassReenter.setText("");
				}
				else if ( !newPass1.equals(newPass2) )
				{
					lblMessage.setVisible(true);
					lblMessage.setText("IDs don't match");

					currentPassEnter.setText("");
					newPassEnter.setText("");
					newPassReenter.setText("");
				}
				else
				{
					lblMessage.setVisible(true);
					lblMessage.setText("ID changed");
					adminPass = newPass1;

					currentPassEnter.setText("");
					newPassEnter.setText("");
					newPassReenter.setText("");
					saveName(newPass1);
				}
			}
		});
		updateID.setBounds(238, 142, 89, 23);
		changeID.add(updateID);

		JPanel loginRecord = new JPanel();
		adminScreen1.addTab("Log in Record", null, loginRecord, null);
		loginRecord.setLayout(null);

		loginRecord.add(loginRecordTable);

	}

	private void create_delete_window(JPanel a)
	{
		JPasswordField confirm = new JPasswordField();
		confirm.setColumns(12);
		confirm.setBounds(175+115, 25, 120, 20);
		a.add(confirm);
		//confirm.getPassword();

		JLabel confirm_lable = new JLabel("Confirm Password: ");
		confirm_lable.setHorizontalAlignment(SwingConstants.LEFT);
		confirm_lable.setBounds(175, 25, 115, 20);
		a.add(confirm_lable);

		JLabel create_section_lable = new JLabel("Create Section");
		create_section_lable.setHorizontalAlignment(SwingConstants.LEFT);
		create_section_lable.setBounds(30, 65, 90, 20);
		a.add(create_section_lable);

		JLabel object_name_lable = new JLabel("Enter Object Name:");
		object_name_lable.setHorizontalAlignment(SwingConstants.LEFT);
		object_name_lable.setBounds(30, 90, 120, 20);
		a.add(object_name_lable);

		JTextField objectName = new JTextField();
		objectName.setHorizontalAlignment(SwingConstants.LEFT);
		objectName.setBounds(30, 115, 120, 20);
		if(create)
			objectName.setEditable(true);
		else if(create == false)
			objectName.setEditable(false);
		a.add(objectName);
		objectName.setColumns(15);

		JLabel amount_label = new JLabel("Initial Amount:");
		amount_label.setHorizontalAlignment(SwingConstants.LEFT);
		amount_label.setBounds(30, 145, 120, 20);
		a.add(amount_label);

		JTextField amount = new JTextField();
		amount.setHorizontalAlignment(SwingConstants.LEFT);
		amount.setBounds(30, 170, 120, 20);
		if (create)
			amount.setEditable(true);
		else
			amount.setEditable(false);
		a.add(amount);
		amount.setColumns(15);

		JLabel min_amount_lable = new JLabel("Minimum Amount:");
		min_amount_lable.setHorizontalAlignment(SwingConstants.LEFT);
		min_amount_lable.setBounds(30, 200, 120, 20);
		a.add(min_amount_lable);

		JTextField minNum = new JTextField();
		minNum.setHorizontalAlignment(SwingConstants.LEFT);
		minNum.setBounds(30, 225, 120, 20);
		if (create)
			minNum.setEditable(true);
		else
			minNum.setEditable(false);
		a.add(minNum);
		minNum.setColumns(15);

		modelAdmin = new DefaultTableModel(disposableItems.size()+nondisposableItems.size(), 2);
		tableItems_allItems(modelAdmin);
		admin_table = new JTable(modelAdmin){
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
		admin_table.setBounds(251, 30, 170, 172);
		admin_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		admin_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane admin_scroll_table = new JScrollPane(admin_table);
		admin_scroll_table.setBounds(322, 70, 90, 135);
	    if(!create)
	    	admin_scroll_table.setVisible(true);
	    else
	    	admin_scroll_table.setVisible(false);
		a.add(admin_scroll_table);


		searchTxt_admin = new JTextField();
		searchTxt_admin.setFont(new java.awt.Font("Tahoma", 0, 12)); // SEARCH FIELD
		searchTxt_admin.setBounds(270, 222, 140, 25);
        searchTxt_admin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchTxtKeyReleased_admin(evt);
            }
        });
        a.add(searchTxt_admin);
        if(!create)
	    	searchTxt_admin.setVisible(true);
	    else
	    	searchTxt_admin.setVisible(false);



	}
}
