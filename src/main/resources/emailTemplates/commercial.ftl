<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Email Corporativo</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
        }

        .container {
            max-width: 700px;
            margin: auto;
            background: #ffffff; /* Centro en blanco */
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 6px rgba(0,0,0,0.15);
        }

        .header {
            background-color: #E0E0E0; /* gris claro */
            text-align: center;
            padding: 20px;
        }

        .header img {
            max-width: 350px; /* logo 20% más grande */
        }

        .body {
            padding: 30px;
            color: #333333;
            font-size: 15px;
            line-height: 1.6;
            text-align: left; /* 🔹 siempre alineado a la izquierda */
        }

        .signature {
            border-top: 2px solid #f37021;
            padding: 20px 30px;
            text-align: left; /* 🔹 firma a la izquierda */
        }

        .signature img {
            max-width: 500px; /* puedes ajustar el tamaño de la firma */
            display: block;   /* 🔹 asegura alineación a la izquierda */
        }

        .footer {
            background: #f9f9f9;
            font-size: 12px;
            text-align: center;
            color: #777;
            padding: 15px 20px;
            border-top: 1px solid #ddd;
        }

        .footer p {
            margin: 4px 0;       /* antes eran ~10px */
            line-height: 1.2;    /* reduce la altura de cada línea */
        }

        .social-links {
            margin: 15px 0;
        }

        .social-links a {
            margin: 0 10px;
            display: inline-block;
        }

        .social-links img {
            width: 24px;
            height: 24px;
            vertical-align: middle;
        }

        .contact-info {
            margin-top: 10px;
            font-size: 12px;
            color: #555;
        }

        .contact-info img {
            width: 14px;
            height: 14px;
            vertical-align: middle;
            margin-right: 5px;
        }

        a {
            color: #f37021;
            text-decoration: none;
        }
    </style>
</head>
<body>
<!-- Fondo gris con tabla -->
<table width="100%" bgcolor="#eaeaea" cellpadding="20" cellspacing="0">
    <tr>
        <td align="center">
            <!-- Contenedor blanco -->
            <div class="container">

                <!-- Encabezado con Logo -->
                <div class="header">
                    <img src="cid:logoId" alt="Logo">
                </div>

                <!-- Cuerpo del correo -->
                <div class="body">
                    ${body} <!-- Aquí Angular inyecta su HTML -->
                </div>

                <!-- Firma como imagen
                <div class="signature">
                    <img src="cid:firma" alt="Signature">
                </div>-->

                <!-- Firma personalizada -->
                <div class="signature" style="padding:20px 40px; border-top:2px solid #f37021;">
                    <table width="100%" cellpadding="0" cellspacing="0" style="border-collapse:collapse;">
                        <tr>
                            <!-- Foto -->
                            <td width="120" align="center" style="padding-right:15px;">
                                <img src="cid:profile" alt="Foto" style="width:100px; height:100px; border-radius:50%; object-fit:cover;">
                            </td>

                            <!-- Línea divisoria naranja -->
                            <td width="2" bgcolor="#f37021"></td>

                            <!-- Info -->
                            <td style="padding-left:15px; color:#333;">
                                <!-- Nombre -->
                                <p style="margin:0; font-size:20px; font-weight:bold; color:#f37021;">${name}</p>

                                <!-- Cargo -->
                                <p style="margin:2px 0 15px 0; font-size:14px; font-weight:600; color:#444;">${title}</p>

                                <!-- Teléfono -->
                                <table cellpadding="0" cellspacing="0" border="0" style="margin:4px 0;">
                                    <tr>
                                        <td style="padding:0 6px 0 0; vertical-align:middle;">
                                            <img src="cid:iconPhone" alt="Phone"
                                                 style="width:20px; height:20px; display:inline-block;">
                                        </td>
                                        <td style="padding:0; font-size:13px; color:#333; vertical-align:middle;">
                                            (305) 507-8496 Ext ${ext}
                                        </td>
                                    </tr>
                                </table>

                                <!-- Email -->
                                <table cellpadding="0" cellspacing="0" border="0" style="margin:4px 0;">
                                    <tr>
                                        <td style="padding:0 6px 0 0; vertical-align:middle;">
                                            <img src="cid:iconEmail" alt="Email"
                                                 style="width:20px; height:20px; display:inline-block;">
                                        </td>
                                        <td style="padding:0; font-size:13px; color:#000 !important; vertical-align:middle;">
                                            <span style="color:#000 !important; text-decoration:none;">${email}</span>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </div>



                <!-- Información adicional / Legal -->
                <div class="footer">
                    <!-- 🔹 Redes sociales con íconos -->
                    <div class="social-links">
                        <a href="https://www.instagram.com/industrialtrading_solutions/" target="_blank">
                            <img src="cid:iconInstagram" alt="Instagram">
                        </a>
                        <a href="https://www.facebook.com/IndustrialTradingSolutions/" target="_blank">
                            <img src="cid:iconFacebook" alt="Facebook">
                        </a>
                        <a href="https://www.linkedin.com/company/industrial-trading-solutions-corp/" target="_blank">
                            <img src="cid:iconLinkedin" alt="LinkedIn">
                        </a>
                    </div>

                    <!-- 🔹 Información de contacto con íconos -->
                    <div class="contact-info">
                        <p><img src="cid:iconAddress" alt="Address"> 1200 Anastasia Ave. Suite150 - Miami, FL, 33134</p>
                        <p><img src="cid:iconWeb" alt="Website"> <a href="www.itradingsolutions.com/" target="_blank">www.itradingsolutions.com</a></p>
                    </div>

                    <p>© ${.now?string("yyyy")} Industrial Trading Solutions</p>
                </div>
            </div>
        </td>
    </tr>
</table>
</body>
</html>
