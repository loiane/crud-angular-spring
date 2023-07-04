/*CREATE DATABASE `courses` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

/*CREATE TABLE `courses`.`course` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `status` varchar(8) NOT NULL,
  `category` varchar(10) NOT NULL,
  `name` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lesson` (
  `id` int NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL,
  `youtube_url` varchar(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjs3c7skmg8bvdddok5lc7s807` (`course_id`),
  CONSTRAINT `FKjs3c7skmg8bvdddok5lc7s807` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
*/
INSERT INTO `courses`.`course` (`id`,`status`,`category`,`name`) VALUES (1,'Active','Back-end','Angular + Spring');

INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'qJnjz8FIs6Q','01: Introdução');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'PqVuSKCk_OU','02: Overview do Projeto e Instalando o Angular Material');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'N4uIR7jgFGA','03: Criando uma Toolbar na Página Principal');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'5QHAtRyiPQ4','04: Criando o Módulo de Cursos e Usando Roteamento com Lazy Loading');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'VnJdZ_od0wY','05: Customizando o Tema do Angular Material');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'LvYXiOh3vZ4','06: Criando Material Table para Listar Cursos');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'jjv5YZhPjfc','07: CSS do Material Table e Criando um Módulo App Material');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'76fUSr1nSDM','08: Criando um Service no Angular');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'LUUn1BWIUA8','09: Chamada HTTP Get no Angular e RXJS');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'vOz_o7oYv9I','10: Lista de Cursos: Spinner (Carregando)');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'gi0ZJ8-r6IM','11: Lista de Cursos: Tratamento de Erros e MatDialog');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'uNFIh3jvp34','12: Lista de Cursos: Pipe para mostrar ícone');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'Ge7Em4byou8','13: Ambiente Java + Maven para Spring');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'-UpIRFONkjs','14: Hello World com Spring');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'w2xOL_yW8Tc','15: Listar Cursos (API HTTP GET)');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'ATjHgBh8dWg','16: Banco de Dados H2 e Conectando o Angular na API Spring');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'OBU_K7jq0nM','17: Update da Versão Angular (ng update) e Spring');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'9_02JxDn_AM','18: Componente de Formulário e Roteamento para criar cursos');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'R3yy3RX4FyM','19: Formulário para Criar Cursos');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'xLhNxqEQnLo','20: Formulário: Salvando os dados com HTTP POST + tratamento de erros');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'R9thmwiG2ns','21: Formulário: API Spring: Criar Curso (HTTP POST)');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'oEawp1Ey3TI','22: Update para o Angular v14');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'9q4tNVeUAao','23: Angular Typed Forms');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'tOIp89BBHgw','24: Refatoração Angular Material Table');
INSERT INTO `courses`.`lesson` (`course_id`,`youtube_url`,`name`) VALUES (1,'3rVmS6psL_U','25: Componentes Inteligentes x Componentes de Apresentação');
