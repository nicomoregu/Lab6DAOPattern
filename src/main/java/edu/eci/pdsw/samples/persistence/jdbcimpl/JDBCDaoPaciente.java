/*
 * Copyright (C) 2015 hcadavid
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
package edu.eci.pdsw.samples.persistence.jdbcimpl;

import com.mysql.fabric.proto.xmlrpc.ResultSetParser;
import edu.eci.pdsw.samples.entities.Consulta;
import edu.eci.pdsw.samples.entities.Paciente;
import edu.eci.pdsw.samples.persistence.DaoPaciente;
import edu.eci.pdsw.samples.persistence.PersistenceException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class JDBCDaoPaciente implements DaoPaciente {

    Connection con;

    public JDBCDaoPaciente(Connection con) {
        this.con = con;
    }
        

    @Override
    public Paciente load(int idpaciente, String tipoid) throws PersistenceException {
        PreparedStatement ps;
        
        
        try {
            String consulta ="select pac.nombre, pac.fecha_nacimiento, con.idCONSULTAS, con.fecha_y_hora, con.resumen "
                    + "from PACIENTES as pac left join CONSULTAS as con on con.PACIENTES_id=pac.id and con.PACIENTES_tipo_id=pac.tipo_id "
                    + "where pac.id=? and pac.tipo_id=?";
            ps=con.prepareCall(consulta);
            ps.setInt(1, idpaciente);
            ps.setString(2,tipoid);
            ResultSet executeQuery = ps.executeQuery();
            Paciente p=null;
            if(executeQuery.next()){
                p = new Paciente(idpaciente, tipoid, executeQuery.getString(1),executeQuery.getDate(2));
            }
            Set<Consulta> lista = new HashSet<>();
            while(executeQuery.next()){
                Consulta c = new Consulta(executeQuery.getDate(4), executeQuery.getString(5));
                lista.add(c);
            }
            p.setConsultas(lista);
            return p;
        } catch (SQLException ex) {
            throw new PersistenceException("An error ocurred while loading "+idpaciente,ex);
        }
        
    }

    @Override
    public void save(Paciente p) throws PersistenceException {
        try {
            con.setAutoCommit(false);
            String consulta = "INSERT INTO PACIENTES VALUES (?,?,?,?)";
            PreparedStatement ps = con.prepareCall(consulta);
            ps.setInt(1,p.getId());
            ps.setString(2, p.getTipo_id());
            ps.setString(3, p.getNombre());
            ps.setDate(4, p.getFechaNacimiento());
            int res= ps.executeUpdate();
            for (Consulta c : p.getConsultas()) {
                consulta ="INSERT INTO CONSULTAS VALUES (?,?,?,?,?)";
                ps = con.prepareCall(consulta);
                ps.setInt(1,c.getId());
                ps.setDate(2,c.getFechayHora());
                ps.setString(3,c.getResumen());
                ps.setInt(4,p.getId());
                ps.setString(5,p.getTipo_id());
                res = ps.executeUpdate();
            }
        } catch (SQLException e){
            e.printStackTrace();
            throw new PersistenceException("Paciente ya existente");
        }
    }

    @Override
    public void update(Paciente p) throws PersistenceException {
        PreparedStatement ps;
        /*try {
            
        } catch (SQLException ex) {
            throw new PersistenceException("An error ocurred while loading a product.",ex);
        } */
        throw new RuntimeException("No se ha implementado el metodo 'load' del DAOPAcienteJDBC");
    }
    
}
