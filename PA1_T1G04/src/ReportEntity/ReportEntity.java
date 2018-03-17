/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReportEntity;

import CollectEntity.Message;
import DigestionEntity.RebalanceListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 *
 * @author kanto
 */
public class ReportEntity extends javax.swing.JFrame {

    /**
     * Creates new form BatchEntity
     */
    public ReportEntity() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                .addGap(23, 23, 23))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private static void createDatabase() {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:src/Data/Report.db");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:src/Data/Report.db");

            Statement stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS MESSAGES (MESSAGE VARCHAR(255)  NOT NULL);";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

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
            java.util.logging.Logger.getLogger(ReportEntity.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReportEntity.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReportEntity.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReportEntity.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ReportEntity().setVisible(true);

                //Create Database
                createDatabase();

                String topicConsumerName = "EnrichedTopic3";
                String groupConsumerName = "EnrichedTopic3Group";

                //Properties
                Properties propsConsumer = new Properties();
                propsConsumer.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094,localhost:9095");
                propsConsumer.put("group.id", groupConsumerName);
                propsConsumer.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                propsConsumer.put("value.deserializer", "CollectEntity.MessageDeserializer");

                KafkaConsumer<String, Message> consumer = new KafkaConsumer<>(propsConsumer);
                RebalanceListener rebalanceListener = new RebalanceListener(consumer);
                consumer.subscribe(Arrays.asList(topicConsumerName), rebalanceListener);

                //Tudo o que aparece dentro da text área é tratado aqui, inclusive o consumidor para outras coisas.
                Thread thread = new Thread() {
                    public void run() {

                        while (true) {
                            ConsumerRecords<String, Message> records = consumer.poll(100);
                            for (ConsumerRecord<String, Message> record : records) {
                                jTextArea1.append(record.value().getMessage() + "\n");

                                //Insert into Database
                                try {
                                    Class.forName("org.sqlite.JDBC");
                                    Connection c = DriverManager.getConnection("jdbc:sqlite:src/Data/Report.db");
                                    c.setAutoCommit(false);

                                    Statement stmt = c.createStatement();
                                    String sql = "INSERT INTO MESSAGES (MESSAGE) VALUES ('"+record.value().getMessage()+"');";
                                    stmt.executeUpdate(sql);
                                    stmt.close();
                                    c.commit();
                                    c.close();
                                } catch (Exception e) {
                                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                                    System.exit(0);
                                }

                                //commit offsets
                                rebalanceListener.addOffset(record.topic(), record.partition(), record.offset());
                            }
                            consumer.commitSync();
                        }
                    }
                };
                thread.start();
            }

        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private static javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
