package com.school.dao;

import com.school.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;

import com.school.model.Estudiante;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EstudianteDao extends JpaRepository<Estudiante, Long>{

    @Query("FROM Estudiante e where e.usuario.username = ?1 and e.usuario.password = ?2 ")
    public Estudiante loginUsuario(String username, String password);

    public Estudiante findByCuiAndCui(String username, String password);

    public Estudiante findByCui(String dni);
    Estudiante findByUsuario_Id(Long usuarioId);
    
    @Query("SELECT e FROM Estudiante e WHERE e.usuario.id = :usuarioId")
    Estudiante findByUsuarioId(@Param("usuarioId") Long usuarioId);
    


}
