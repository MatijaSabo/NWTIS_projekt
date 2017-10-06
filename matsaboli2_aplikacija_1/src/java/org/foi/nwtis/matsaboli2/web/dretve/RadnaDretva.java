/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.dretve;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import org.foi.nwtis.matsaboli2.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.rest.klijenti.GMKlijent;
import org.foi.nwtis.matsaboli2.web.podaci.Lokacija;
import org.foi.nwtis.matsaboli2.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.matsaboli2.ws.IoT_Master_Client;
import org.foi.nwtis.matsaboli2.ws.StatusUredjaja;
import org.foi.nwtis.matsaboli2.ws.Uredjaj;

/**
 *
 * @author Matija
 */
public class RadnaDretva extends Thread {

    Socket kor_socket = null;

    String komanda;
    String komanda_user;
    String komanda_passwd;
    int komanda_IoT_id;
    String komanda_IoT_adresa;
    String komanda_IoT_naziv;

    Date vrijeme_preuzimanja;

    public RadnaDretva(Socket socket, Date vrijeme_preuzimanja) {
        this.kor_socket = socket;
        this.vrijeme_preuzimanja = vrijeme_preuzimanja;
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public void run() {
        InputStream is = null;
        OutputStream os = null;

        try {
            is = kor_socket.getInputStream();
            os = kor_socket.getOutputStream();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }

                sb.append((char) znak);
            }

            this.komanda = sb.toString().trim();
            int command_status = testCommand(this.komanda);
            String action_status = "";

            switch (command_status) {
                case -1:
                    if (!ServerSustava.server_stop) {
                        action_status = "ERR 10;";
                    }
                    break;
                case 1:
                    action_status = PrekiniPreuzimanjeMeteoPodataka();
                    break;
                case 2:
                    action_status = NastaviPreuzimanjeMeteoPodataka();
                    break;
                case 3:
                    action_status = UgasiServer();
                    break;
                case 4:
                    action_status = DajStatusServera();
                    break;
                case 5:
                    action_status = RegistrirajGrupu();
                    break;
                case 6:
                    action_status = DeregistrirajGrupu();
                    break;
                case 7:
                    action_status = AktivirajGrupu();
                    break;
                case 8:
                    action_status = BlokirajGrupu();
                    break;
                case 9:
                    action_status = UcitajPredefiniranjeUredaje();
                    break;
                case 10:
                    action_status = ObrisiSveUredaje();
                    break;
                case 11:
                    action_status = DajStatusGrupe();
                    break;
                case 12:
                    action_status = DajSveUredajeGrupe();
                    break;
                case 13:
                    action_status = DodajUredajGrupi();
                    break;
                case 14:
                    action_status = AktivirajUredajGrupe();
                    break;
                case 15:
                    action_status = BlokirajUredajGrupe();
                    break;
                case 16:
                    action_status = ObrisiUredajGrupe();
                    break;
                case 17:
                    action_status = DajStatusUredaja();
                    break;
                default:
                    if (!ServerSustava.server_stop) {
                        action_status = "ERR;";
                    }
                    break;
            }

            os.write(action_status.getBytes());
            os.flush();
            
            dnevnik();

        } catch (IOException ex) {
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }

                if (kor_socket != null) {
                    kor_socket.close();
                }
            } catch (IOException ex) {
                System.out.println("ERROR; Problem kod zatvaranja socketa");
            }
        }

    }

    @Override
    public synchronized void start() {
        super.start();
    }

    private int testCommand(String acepted_command) {
        String sintaksa_1 = "^USER ([^\\s]+); PASSWD ([^\\s]+); (PAUSE|STOP|START|STATUS);$";
        String sintaksa_2 = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master (START|STOP|WORK|WAIT|LOAD|CLEAR|STATUS|LIST);$";
        String sintaksa_3 = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT ([0-9]{1,6}) (ADD \'([\\s\\S]+)\' \'([\\s\\S]+)\'|WORK|STATUS|WAIT|REMOVE);$";

        String[] polje = acepted_command.split(" ");

        if ("USER".equals(polje[0]) && "PASSWD".equals(polje[2])) {
            int user_status = testUser(polje[1], polje[3]);

            if (user_status == 1) {
                Pattern pattern1 = Pattern.compile(sintaksa_1);
                Matcher m1 = pattern1.matcher(acepted_command);
                boolean valid_1 = m1.matches();

                Pattern pattern2 = Pattern.compile(sintaksa_2);
                Matcher m2 = pattern2.matcher(acepted_command);
                boolean valid_2 = m2.matches();

                Pattern pattern3 = Pattern.compile(sintaksa_3);
                Matcher m3 = pattern3.matcher(acepted_command);
                boolean valid_3 = m3.matches();

                if (valid_1) {
                    if ("PAUSE;".equals(polje[4])) {
                        komanda_user = m1.group(1);
                        komanda_passwd = m1.group(2);
                        return 1;
                    } else if ("START;".equals(polje[4])) {
                        komanda_user = m1.group(1);
                        komanda_passwd = m1.group(2);
                        return 2;
                    } else if ("STOP;".equals(polje[4])) {
                        komanda_user = m1.group(1);
                        komanda_passwd = m1.group(2);
                        return 3;
                    } else {
                        komanda_user = m1.group(1);
                        komanda_passwd = m1.group(2);
                        return 4;
                    }
                } else if (valid_2) {
                    if ("START;".equals(polje[5])) {
                        komanda_user = m2.group(1);
                        komanda_passwd = m2.group(2);
                        return 5;
                    } else if ("STOP;".equals(polje[5])) {
                        komanda_user = m2.group(1);
                        komanda_passwd = m2.group(2);
                        return 6;
                    } else if ("WORK;".equals(polje[5])) {
                        komanda_user = m2.group(1);
                        komanda_passwd = m2.group(2);
                        return 7;
                    } else if ("WAIT;".equals(polje[5])) {
                        komanda_user = m2.group(1);
                        komanda_passwd = m2.group(2);
                        return 8;
                    } else if ("LOAD;".equals(polje[5])) {
                        komanda_user = m2.group(1);
                        komanda_passwd = m2.group(2);
                        return 9;
                    } else if ("CLEAR;".equals(polje[5])) {
                        komanda_user = m2.group(1);
                        komanda_passwd = m2.group(2);
                        return 10;
                    } else if ("STATUS;".equals(polje[5])) {
                        komanda_user = m2.group(1);
                        komanda_passwd = m2.group(2);
                        return 11;
                    } else {
                        komanda_user = m2.group(1);
                        komanda_passwd = m2.group(2);
                        return 12;
                    }
                } else if (valid_3) {
                    if ("ADD".equals(polje[6])) {
                        komanda_user = m3.group(1);
                        komanda_passwd = m3.group(2);
                        komanda_IoT_id = Integer.parseInt(m3.group(3));
                        komanda_IoT_naziv = m3.group(5);
                        komanda_IoT_adresa = m3.group(6);
                        return 13;
                    } else if ("WORK;".equals(polje[6])) {
                        komanda_user = m3.group(1);
                        komanda_passwd = m3.group(2);
                        komanda_IoT_id = Integer.parseInt(m3.group(3));
                        return 14;
                    } else if ("WAIT;".equals(polje[6])) {
                        komanda_user = m3.group(1);
                        komanda_passwd = m3.group(2);
                        komanda_IoT_id = Integer.parseInt(m3.group(3));
                        return 15;
                    } else if ("REMOVE;".equals(polje[6])) {
                        komanda_user = m3.group(1);
                        komanda_passwd = m3.group(2);
                        komanda_IoT_id = Integer.parseInt(m3.group(3));
                        return 16;
                    } else {
                        komanda_user = m3.group(1);
                        komanda_passwd = m3.group(2);
                        komanda_IoT_id = Integer.parseInt(m3.group(3));
                        return 17;
                    }
                } else {
                    return -1;
                }
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    private int testUser(String kor_ime, String pass) {
        kor_ime = kor_ime.substring(0, kor_ime.length() - 1);
        pass = pass.substring(0, pass.length() - 1);

        ServletContext sc = (ServletContext) SlusacAplikacije.kontekst;
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        Connection veza = null;

        try {
            Class.forName(bp_driver);
            veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);

            PreparedStatement select = veza.prepareStatement("SELECT * FROM korisnici WHERE kor_ime = ? AND pass = ?");
            select.setString(1, kor_ime);
            select.setString(2, pass);
            ResultSet result = select.executeQuery();
            if (result.next()) {
                return 1;
            } else {
                return 0;
            }
        } catch (ClassNotFoundException | SQLException ex) {
            return 0;
        }
    }

    public void saljiPoruku() {
        ServletContext sc = (ServletContext) SlusacAplikacije.kontekst;
        Konfiguracija konf = (Konfiguracija) sc.getAttribute("App_Konfig");

        String server = konf.dajPostavku("mail.server");
        String primatelj = konf.dajPostavku("mail.username");
        String subject = konf.dajPostavku("mail.subject");

        try {
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", server);

            Session session = Session.getInstance(properties, null);

            MimeMessage message = new MimeMessage(session);

            Address fromAddress = new InternetAddress("radnaDretva@nwtis.nastava.foi.hr");
            message.setFrom(fromAddress);

            Address[] toAddresses = InternetAddress.parse(primatelj);
            message.setRecipients(Message.RecipientType.TO, toAddresses);

            message.setSubject(subject);

            String pocetak = new SimpleDateFormat("dd.MM.yyyy hh.mm.ss.zzz").format(this.vrijeme_preuzimanja);
            String sadrzaj = this.komanda + " \n";
            sadrzaj = sadrzaj + "Vrijeme preuzimanja komande: " + pocetak;

            message.setText(sadrzaj);
            message.setSentDate(new Date());

            Transport.send(message);

        } catch (AddressException e) {
        } catch (SendFailedException e) {
        } catch (MessagingException e) {
        }
    }

    private String PrekiniPreuzimanjeMeteoPodataka() {
        if (!ServerSustava.server_stop) {
            if (!PreuzimanjePrognoza.meteo_pause) {
                PreuzimanjePrognoza.meteo_pause = true;
                saljiPoruku();
                return "OK 10;";
            } else {
                saljiPoruku();
                return "ERR 10;";
            }
        } else {
            return "ERR;";
        }
    }

    private String NastaviPreuzimanjeMeteoPodataka() {
        if (!ServerSustava.server_stop) {
            if (PreuzimanjePrognoza.meteo_pause) {
                PreuzimanjePrognoza.meteo_pause = false;
                saljiPoruku();
                return "OK 10;";
            } else {
                saljiPoruku();
                return "ERR 11;";
            }
        } else {
            return "ERR;";
        }
    }

    private String UgasiServer() throws IOException {
        if (!ServerSustava.server_stop) {
            ServerSustava.server_stop = true;
            PreuzimanjePrognoza.meteo_pause = true;

            ServerSustava.ss.close();

            saljiPoruku();
            return "OK 10;";
        } else {
            saljiPoruku();
            return "ERR 12;";
        }
    }

    private String DajStatusServera() {
        if (!ServerSustava.server_stop) {
            if (PreuzimanjePrognoza.meteo_pause) {
                saljiPoruku();
                return "OK 13;";
            } else {
                saljiPoruku();
                return "OK 14;";
            }
        } else {
            saljiPoruku();
            return "OK 15;";
        }
    }

    private String RegistrirajGrupu() {
        if (!ServerSustava.server_stop) {
            boolean user_status = IoT_Master_Client.autenticirajGrupuIoT(this.komanda_user, this.komanda_passwd);

            if (user_status) {
                boolean status = IoT_Master_Client.registrirajGrupuIoT(this.komanda_user, this.komanda_passwd);
                if (status) {
                    return "OK 10;";
                } else {
                    return "ERR 20;";
                }
            } else {
                return "ERR;";
            }
        } else {
            return "ERR";
        }
    }

    private String DeregistrirajGrupu() {
        if (!ServerSustava.server_stop) {
            boolean user_status = IoT_Master_Client.autenticirajGrupuIoT(this.komanda_user, this.komanda_passwd);

            if (user_status) {
                boolean status = IoT_Master_Client.deregistrirajGrupuIoT(this.komanda_user, this.komanda_passwd);
                if (status) {
                    return "OK 10;";
                } else {
                    return "ERR 21;";
                }
            } else {
                return "ERR;";
            }
        } else {
            return "ERR;";
        }
    }

    private String AktivirajGrupu() {
        if (!ServerSustava.server_stop) {
            boolean user_status = IoT_Master_Client.autenticirajGrupuIoT(this.komanda_user, this.komanda_passwd);

            if (user_status) {
                boolean status = IoT_Master_Client.aktivirajGrupuIoT(this.komanda_user, this.komanda_passwd);
                if (status) {
                    return "OK 10;";
                } else {
                    return "ERR 22;";
                }
            } else {
                return "ERR;";
            }
        } else {
            return "ERR;";
        }
    }

    private String BlokirajGrupu() {
        if (!ServerSustava.server_stop) {
            boolean user_status = IoT_Master_Client.autenticirajGrupuIoT(this.komanda_user, this.komanda_passwd);

            if (user_status) {
                boolean status = IoT_Master_Client.blokirajGrupuIoT(this.komanda_user, this.komanda_passwd);
                if (status) {
                    return "OK 10;";
                } else {
                    return "ERR 23;";
                }
            } else {
                return "ERR;";
            }
        } else {
            return "ERR;";
        }
    }

    private String UcitajPredefiniranjeUredaje() {
        if (!ServerSustava.server_stop) {
            boolean user_status = IoT_Master_Client.autenticirajGrupuIoT(this.komanda_user, this.komanda_passwd);

            if (user_status) {
                boolean status = IoT_Master_Client.ucitajSveUredjajeGrupe(this.komanda_user, this.komanda_passwd);
                if (status) {
                    return "OK 10;";
                } else {
                    return "ERR;";
                }
            } else {
                return "ERR;";
            }
        } else {
            return "ERR";
        }
    }

    private String ObrisiSveUredaje() {
        if (!ServerSustava.server_stop) {
            boolean user_status = IoT_Master_Client.autenticirajGrupuIoT(this.komanda_user, this.komanda_passwd);

            if (user_status) {
                boolean status = IoT_Master_Client.obrisiSveUredjajeGrupe(this.komanda_user, this.komanda_passwd);
                if (status) {
                    return "OK 10;";
                } else {
                    return "ERR;";
                }
            } else {
                return "ERR;";
            }
        } else {
            return "ERR;";
        }
    }

    private String DajStatusGrupe() {
        if (!ServerSustava.server_stop) {
            boolean user_status = IoT_Master_Client.autenticirajGrupuIoT(this.komanda_user, this.komanda_passwd);

            if (user_status) {
                String status = IoT_Master_Client.dajStatusGrupeIoT(this.komanda_user, this.komanda_passwd).value();
                if ("BLOKIRAN".equals(status)) {
                    return "OK 24;";
                } else if ("AKTIVAN".equals(status)) {
                    return "OK 25;";
                } else {
                    return "OK;";
                }
            } else {
                return "ERR;";
            }
        } else {
            return "ERR;";
        }
    }

    private String DajSveUredajeGrupe() {
        if (!ServerSustava.server_stop) {
            boolean user_status = IoT_Master_Client.autenticirajGrupuIoT(this.komanda_user, this.komanda_passwd);

            if (user_status) {
                List<Uredjaj> lista = IoT_Master_Client.dajSveUredjajeGrupe(this.komanda_user, this.komanda_passwd);

                String action_status = "OK 10; {";

                for (int i = 0; i < lista.size(); i++) {
                    if ((lista.size() - 1) == i) {
                        action_status = action_status + "IoT '" + lista.get(i).getId() + "' '" + lista.get(i).getNaziv() + "'";
                    } else {
                        action_status = action_status + "IoT '" + lista.get(i).getId() + "' '" + lista.get(i).getNaziv() + "', ";
                    }
                }

                action_status = action_status + "}";

                return action_status;
            } else {
                return "ERR;";
            }
        } else {
            return "ERR;";
        }
    }

    private String DodajUredajGrupi() {
        if (!ServerSustava.server_stop) {
            boolean user_status = IoT_Master_Client.autenticirajGrupuIoT(this.komanda_user, this.komanda_passwd);

            if (user_status) {
                GMKlijent gm = new GMKlijent();
                Lokacija lokacija = gm.getGeoLocation(this.komanda_IoT_adresa);

                org.foi.nwtis.matsaboli2.ws.Lokacija lok = new org.foi.nwtis.matsaboli2.ws.Lokacija();
                lok.setLatitude(lokacija.getLatitude());
                lok.setLongitude(lokacija.getLongitude());

                Uredjaj uredjaj = new Uredjaj();
                uredjaj.setId(this.komanda_IoT_id);
                uredjaj.setNaziv(this.komanda_IoT_naziv);
                uredjaj.setStatus(StatusUredjaja.BLOKIRAN);
                uredjaj.setGeoloc(lok);

                boolean status = IoT_Master_Client.dodajUredjajGrupi(this.komanda_user, this.komanda_passwd, uredjaj);
                if (status) {
                    return "OK 10;";
                } else {
                    return "ERR 30;";
                }
            } else {
                return "ERR;";
            }
        } else {
            return "ERR;";
        }
    }

    private String AktivirajUredajGrupe() {
        if (!ServerSustava.server_stop) {
            boolean user_status = IoT_Master_Client.autenticirajGrupuIoT(this.komanda_user, this.komanda_passwd);

            if (user_status) {
                boolean status = IoT_Master_Client.aktivirajUredjajGrupe(this.komanda_user, this.komanda_passwd, this.komanda_IoT_id);
                if (status) {
                    return "OK 10;";
                } else {
                    return "ERR 31;";
                }
            } else {
                return "ERR;";
            }
        } else {
            return "ERR;";
        }
    }

    private String BlokirajUredajGrupe() {
        if (!ServerSustava.server_stop) {
            boolean user_status = IoT_Master_Client.autenticirajGrupuIoT(this.komanda_user, this.komanda_passwd);

            if (user_status) {
                boolean status = IoT_Master_Client.blokirajUredjajGrupe(this.komanda_user, this.komanda_passwd, this.komanda_IoT_id);
                if (status) {
                    return "OK 10;";
                } else {
                    return "ERR 32;";
                }
            } else {
                return "ERR;";
            }
        } else {
            return "ERR;";
        }
    }

    private String ObrisiUredajGrupe() {
        if (!ServerSustava.server_stop) {
            boolean user_status = IoT_Master_Client.autenticirajGrupuIoT(this.komanda_user, this.komanda_passwd);

            if (user_status) {
                boolean status = IoT_Master_Client.obrisiUredjajGrupe(this.komanda_user, this.komanda_passwd, this.komanda_IoT_id);
                if (status) {
                    return "OK 10;";
                } else {
                    return "ERR 33;";
                }
            } else {
                return "ERR;";
            }
        } else {
            return "ERR;";
        }
    }

    private String DajStatusUredaja() {
        if (!ServerSustava.server_stop) {
            boolean user_status = IoT_Master_Client.autenticirajGrupuIoT(this.komanda_user, this.komanda_passwd);

            if (user_status) {
                String status = (String) IoT_Master_Client.dajStatusUredjajaGrupe(this.komanda_user, this.komanda_passwd, this.komanda_IoT_id).value();
                if ("BLOKIRAN".equals(status)) {
                    saljiPoruku();
                    return "OK 34;";
                } else if ("AKTIVAN".equals(status)) {
                    return "OK 35;";
                } else {
                    return "ERR;";
                }
            } else {
                return "ERR";
            }
        } else {
            return "ERR;";
        }
    }
    
    private void dnevnik(){
        ServletContext sc = (ServletContext) SlusacAplikacije.kontekst;
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

        String bp_server = bp_konf.getServerDatabase();
        String bp_baza = bp_server + bp_konf.getUserDatabase();
        String bp_korisnik = bp_konf.getUserUsername();
        String bp_lozinka = bp_konf.getUserPassword();
        String bp_driver = bp_konf.getDriverDatabase();

        Connection veza = null;

        try {
            Class.forName(bp_driver);
            veza = DriverManager.getConnection(bp_baza, bp_korisnik, bp_lozinka);
            
            String url = kor_socket.getRemoteSocketAddress().toString();

            PreparedStatement dnevnik = veza.prepareStatement("INSERT INTO dnevnik (id, kor_ime, akcija, url, vrijeme, status) VALUES (default,?,?,?,default,?)");
            dnevnik.setString(1, this.komanda_user);
            dnevnik.setString(2, this.komanda);
            dnevnik.setString(3, url);
            dnevnik.setInt(4, 1);
            dnevnik.executeUpdate();

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}